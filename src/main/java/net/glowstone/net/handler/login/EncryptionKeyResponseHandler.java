package net.glowstone.net.handler.login;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.entity.meta.PlayerProperty;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.util.UuidUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        SecretKey sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = new SecretKeySpec(rsaCipher.doFinal(message.getSharedSecret()), "AES");
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
        if (!Arrays.equals(verifyToken, session.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

        // initialize stream encryption
        session.enableEncryption(sharedSecret);

        // create hash for auth
        String hash;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(session.getSessionId().getBytes());
            digest.update(sharedSecret.getEncoded());
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
                JSONObject json;
                try (InputStream is = conn.getInputStream()) {
                    try {
                        json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
                    } catch (ParseException e) {
                        GlowServer.logger.warning("Username \"" + username + "\" failed to authenticate!");
                        session.disconnect("Failed to verify username!");
                        return;
                    }
                }

                final String name = (String) json.get("name");
                final String id = (String) json.get("id");
                final JSONArray propsArray = (JSONArray) json.get("properties");

                // Parse UUID
                final UUID uuid;
                try {
                    uuid = UuidUtils.fromFlatString(id);
                } catch (IllegalArgumentException ex) {
                    GlowServer.logger.log(Level.SEVERE, "Returned authentication UUID invalid: " + id, ex);
                    session.disconnect("Invalid UUID.");
                    return;
                }

                // Parse properties
                final List<PlayerProperty> properties = new ArrayList<>(propsArray.size());
                for (Object obj : propsArray) {
                    JSONObject propJson = (JSONObject) obj;
                    String propName = (String) propJson.get("name");
                    String value = (String) propJson.get("value");
                    String signature = (String) propJson.get("signature");
                    properties.add(new PlayerProperty(propName, value, signature));
                }

                // Spawn in player
                session.getServer().getScheduler().runTask(null, new Runnable() {
                    @Override
                    public void run() {
                        session.setPlayer(new PlayerProfile(name, uuid, properties));
                    }
                });
            } catch (Exception e) {
                GlowServer.logger.log(Level.SEVERE, "Error in authentication thread", e);
                session.disconnect("Internal error during authentication.");
            }
        }
    }
}
