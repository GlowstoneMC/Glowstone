package net.glowstone.util;

import net.glowstone.GlowServer;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

/**
 * Utility class for performing encrypted authentication.
 */
public final class SecurityUtils {

    private static SecureRandom random = new SecureRandom();

    private SecurityUtils() {
    }

    /**
     * Generate a RSA key pair.
     *
     * @return The RSA key pair.
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(3072);

            keyPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate RSA key pair", ex);
        }
        return keyPair;
    }

    /**
     * Generate a random verify token.
     *
     * @return An array of 4 random bytes.
     */
    public static byte[] generateVerifyToken() {
        byte[] token = new byte[4];
        random.nextBytes(token);
        return token;
    }

    /**
     * Generates an X509 formatted key used in authentication.
     *
     * @param base The key to use to generate a public key from its key spec.
     * @return The X509 formatted key.
     */
    public static Key generateX509Key(Key base) {
        Key key = null;
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(base.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            key = keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate X509 encoded key", ex);
        }
        return key;
    }
}
