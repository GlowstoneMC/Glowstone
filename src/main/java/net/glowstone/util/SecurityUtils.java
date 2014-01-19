package net.glowstone.util;

import net.glowstone.GlowServer;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

/**
 * Utility class for performing encrypted authentication
 */
public class SecurityUtils {

    private static SecureRandom random = new SecureRandom();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generate a RSA key pair
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);

            keyPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate RSA key pair: {0}", ex.getMessage());
        }
        return keyPair;
    }

    /**
     * Generate a random verify token
     */
    public static byte[] generateVerifyToken() {
        byte[] token = new byte[4];
        random.nextBytes(token);
        return token;
    }

    /**
     * Generates an AES cipher for use with authentication
     */
    public static Cipher generateAESCipher(int opMode, Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
        } catch (GeneralSecurityException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate AES cipher: {0}", ex.getMessage());
        }
        return cipher;
    }

    /**
     * Generates an RSA cipher for use with authentication
     */
    public static Cipher generateRSACipher(int opMode, Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(opMode, key);
        } catch (GeneralSecurityException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate RSA cipher: {0}", ex.getMessage());
        }
        return cipher;
    }



    /**
     * Generates a message digest used in hashing
     */
    public static MessageDigest generateSHA1MessageDigest() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate SHA-1 digest: {0}", ex.getMessage());
        }
        return digest;
    }

    /**
     * Generates an X509 formatted key used in authentication
     */
    public static Key generateX509Key(Key base) {
        Key key = null;
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(base.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            key = keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to generate X509 encoded key: {0}", ex.getMessage());
        }
        return key;
    }

    /**
     * Generates a BufferedBlockCipher used in encryption
     */
    public static BufferedBlockCipher generateBouncyCastleAESCipher() {
        BufferedBlockCipher cipher = null;

        cipher = new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), 8));

        return cipher;
    }
}
