package net.glowstone.net.handler.login;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.EncryptionChannelProcessor;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.util.UuidUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public final class EncryptionKeyResponseHandler implements MessageHandler<GlowSession, EncryptionKeyResponseMessage> {

    @Override
    public void handle(GlowSession session, EncryptionKeyResponseMessage message) {
        final PrivateKey privateKey = session.getServer().getKeyPair().getPrivate();

        // create rsaCipher
        Cipher rsaCipher;
        try {
            rsaCipher = Cipher.getInstance("RSA");
        } catch (GeneralSecurityException ex) {
            GlowServer.logger.log(Level.SEVERE, "Could not initialize RSA cipher", ex);
            session.disconnect("Unable to initialize RSA cipher.");
            return;
        }

        // decrypt shared secret
        byte[] sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = rsaCipher.doFinal(message.getSharedSecret());
        } catch (Exception ex) {
            GlowServer.logger.log(Level.WARNING, "Could not decrypt shared secret", ex);
            session.disconnect("Unable to decrypt shared secret.");
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

        // check verify token
        if(!Arrays.equals(verifyToken, session.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

        // initialize stream encryption
        session.setProcessor(new EncryptionChannelProcessor(sharedSecret, 32));

        // create hash for auth
        String hash;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(session.getSessionId().getBytes());
            digest.update(sharedSecret);
            digest.update(session.getServer().getKeyPair().getPublic().getEncoded());

            // BigInteger takes care of sign and leading zeroes
            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate SHA-1 digest", ex);
            session.disconnect("Failed to hash login data.");
            return;
        }

        // start auth thread
        new ClientAuthThread(session, session.getVerifyUsername(), hash).start();
    }

    private static class ClientAuthThread extends Thread {

        private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

        private final GlowSession session;
        private final String username;
        private final String postURL;

        private ClientAuthThread(GlowSession session, String username, String hash) {
            this.session = session;
            this.username = username;
            this.postURL = BASE_URL + "?username=" + username + "&serverId=" + hash;
            setName("ClientAuthThread{" + username + "}");
        }

        @Override
        public void run() {
            try {
                URLConnection conn = new URL(postURL).openConnection();
                InputStream is = conn.getInputStream();
                JSONObject json;
                try {
                    json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
                } catch (ParseException e) {
                    session.disconnect("Failed to verify username!");
                    GlowServer.logger.warning("Username \"" + username + "\" failed to authenticate!");
                    return;
                }

                String id = (String) json.get("id");

                final UUID uuid;
                try {
                    uuid = UuidUtils.fromFlatString(id);
                } catch (IllegalArgumentException ex) {
                    GlowServer.logger.log(Level.SEVERE, "Returned authentication UUID invalid: {0}", ex.getMessage());
                    session.disconnect("Invalid UUID.");
                    return;
                }

                session.getServer().getScheduler().runTask(null, new Runnable() {
                    @Override
                    public void run() {
                        session.setPlayer(new GlowPlayer(session, username, uuid));
                    }
                });
            } catch (Exception e) {
                GlowServer.logger.log(Level.SEVERE, "Error in authentication thread", e);
                session.disconnect("Internal error during authentication.");
            }
        }
    }
}
