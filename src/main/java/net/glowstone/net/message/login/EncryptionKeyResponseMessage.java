package net.glowstone.net.message.login;

import net.glowstone.GlowServer;
import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.logging.Level;

public final class EncryptionKeyResponseMessage extends Message {

    private final byte[] sharedSecret;
    private final byte[] verifyToken;

    public EncryptionKeyResponseMessage(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    public EncryptionKeyResponseMessage(ChannelBuffer buf) {
        GlowServer.logger.log(Level.INFO, "Decoding encryption response");

        short len = buf.readShort();
        GlowServer.logger.log(Level.INFO, "First length: {0}", len);

        this.sharedSecret = new byte[len];
        buf.readBytes(sharedSecret);

        short len2 = buf.readShort();
        GlowServer.logger.log(Level.INFO, "second length: {0}", len);

        this.verifyToken = new byte[len2];
        buf.readBytes(verifyToken);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeShort(sharedSecret.length);
        buf.writeBytes(sharedSecret);

        buf.writeShort(verifyToken.length);
        buf.writeBytes(verifyToken);
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
