package net.glowstone.net.handler.login;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
//import net.glowstone.net.EncryptionChannelProcessor;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.util.SecurityUtils;
import net.glowstone.util.UuidUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public final class EncryptionKeyResponseHandler implements MessageHandler<GlowSession, EncryptionKeyResponseMessage> {

    @Override
    public void handle(GlowSession session, EncryptionKeyResponseMessage message) {
        final PrivateKey privateKey = session.getServer().getKeyPair().getPrivate();
        final Cipher rsaCipher = SecurityUtils.generateRSACipher(Cipher.DECRYPT_MODE, privateKey);

        // decrypt shared secret
        byte[] sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = rsaCipher.doFinal(message.getSharedSecret());
        } catch (Exception ex) {
            GlowServer.logger.log(Level.WARNING, "Could not decrypt shared secret", ex);
            session.disconnect("Unable to decrypt verify token.");
            return;
        }

        // decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception ex) {
            GlowServer.logger.log(Level.WARNING, "Could not decrypt verify token", ex);
            session.disconnect("Unable to decrypt verify token.");
            return;
        }

        if(!Arrays.equals(verifyToken, session.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

//        BufferedBlockCipher encodeCipher = SecurityUtils.generateBouncyCastleAESCipher();
//        CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(sharedSecret), sharedSecret);
//        encodeCipher.init(false, symmetricKey);
//
//        BufferedBlockCipher decodeCipher = SecurityUtils.generateBouncyCastleAESCipher();
//        CipherParameters symmetricKey2 = new ParametersWithIV(new KeyParameter(sharedSecret), sharedSecret);
//        encodeCipher.init(true, symmetricKey2);
//
//        EncryptionChannelProcessor processor = new EncryptionChannelProcessor(encodeCipher, decodeCipher, 32);
//        session.setProcessor(processor);

        // todo: at this point, the stream encryption should be enabled

        //Create our hash to be used in the authentication post.
        final MessageDigest digest = SecurityUtils.generateSHA1MessageDigest();
        digest.update(session.getSessionId().getBytes());
        digest.update(sharedSecret);
        digest.update(session.getServer().getKeyPair().getPublic().getEncoded());

        final String hash = DatatypeConverter.printHexBinary(digest.digest());

        ClientAuthentication clientAuth = new ClientAuthentication(session.getVerifyUsername(), hash, session);
        new Thread(clientAuth).start();
    }

    private class ClientAuthentication implements Runnable {

        private final String baseURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
        private final String username;
        private final String hash;
        private final String postURL;

        private final GlowSession session;

        private ClientAuthentication(String username, String hash, GlowSession session) {
            this.username = username;
            this.hash = hash;
            this.postURL = baseURL + "?username=" + username + "&serverId=" + hash;
            this.session = session;
        }

        @Override
        public void run() {
            URLConnection conn;

            try {
                URL url = new URL(postURL);
                conn = url.openConnection();

                InputStream is = conn.getInputStream();
                JSONObject json;
                try {
                    json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
                } catch (ParseException e) {
                    session.disconnect("Authentication failed.");
                    return;
                }
                System.out.println(json.toJSONString());

                final String id = (String) json.get("id");

                session.getServer().getScheduler().runTask(null, new Runnable() {
                    @Override
                    public void run() {
                        UUID uuid;

                        try {
                            uuid = UuidUtils.fromFlatString(id);
                        } catch (IllegalArgumentException ex) {
                            GlowServer.logger.log(Level.SEVERE, "Returned authentication UUID invalid: {0}", ex.getMessage());
                            session.disconnect("Invalid UUID.");
                            return;
                        }

                        session.setPlayer(new GlowPlayer(session, username, uuid));
                    }
                });
            } catch (Exception e) {
                session.disconnect("Internal error during authentication.");
                GlowServer.logger.log(Level.SEVERE, "Error in authentication thread", e);
            }
        }
    }
}
