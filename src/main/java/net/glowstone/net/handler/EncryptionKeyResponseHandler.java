package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.util.SecurityUtils;
import net.glowstone.util.UuidUtils;
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

public class EncryptionKeyResponseHandler extends MessageHandler<EncryptionKeyResponseMessage>{
    @Override
    public void handle(Session session, GlowPlayer player, EncryptionKeyResponseMessage message) {
        GlowServer.logger.log(Level.INFO, "Begin handle encryption response");


        final PrivateKey privateKey = session.getServer().getKeyPair().getPrivate();
        final Cipher rsaCipher = SecurityUtils.generateRSACipher(Cipher.DECRYPT_MODE, privateKey);

        GlowServer.logger.log(Level.INFO, "Created cipher using private rsa key");
        GlowServer.logger.log(Level.INFO, "Shared secret: {0}, length {1}", new Object[] {message.getSharedSecret(), message.getSharedSecret().length});
        GlowServer.logger.log(Level.INFO, "Verify Token: {0}, length {1}", new Object[] {message.getVerifyToken(), message.getVerifyToken().length});

        // decrypt shared secret
        byte[] sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = rsaCipher.doFinal(message.getSharedSecret());
        } catch (Exception ex) {
            GlowServer.logger.log(Level.WARNING, "Could not decrypt shared secret", ex);
            session.disconnect("Could not decrypt shared secret");
            return;
        }

        // decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception ex) {
            GlowServer.logger.log(Level.WARNING, "Could not decrypt verify token", ex);
            session.disconnect("Could not decrypt Verify token");
            return;
        }

        if(!Arrays.equals(verifyToken, session.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

        GlowServer.logger.log(Level.INFO, "Decrypted shared secret and verify token");

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

        private final Session session;

        private ClientAuthentication(String username, String hash, Session session) {
            this.username = username;
            this.hash = hash;
            this.postURL = baseURL + "?username=" + username + "&serverId=" + hash;
            this.session = session;
        }

        @Override
        public void run() {
            GlowServer.logger.log(Level.INFO, "ClientAuth started");

            URLConnection conn;

            try {
                URL url = new URL(postURL);
                conn = url.openConnection();

                InputStream is = conn.getInputStream();
                JSONObject json;
                try {
                    json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
                } catch (ParseException e) {
                    session.disconnect("Authentication failed");
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
                            GlowServer.logger.log(Level.SEVERE, "Returned authentication uuid invalid: {0}", ex.getMessage());
                            session.disconnect("Invalid UUID.");
                            return;
                        }

                        session.setPlayer(new GlowPlayer(session, username, uuid));
                    }
                });
            } catch (Exception e) {
                session.disconnect("Internal error during authentication");
                GlowServer.logger.log(Level.SEVERE, "Error in authentication thread", e);
            }
        }
    }
}
