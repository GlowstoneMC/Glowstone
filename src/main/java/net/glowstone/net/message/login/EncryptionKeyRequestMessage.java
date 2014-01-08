package net.glowstone.net.message.login;

import net.glowstone.net.message.Message;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public final class EncryptionKeyRequestMessage extends Message {

    private final String sessionId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

    public EncryptionKeyRequestMessage(String sessionId, byte[] publicKey, byte[] verifyToken) {
        this.sessionId = sessionId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, sessionId);

        buf.writeShort(publicKey.length);
        buf.writeBytes(publicKey);
        buf.writeShort(verifyToken.length);
        buf.writeBytes(verifyToken);
    }
}
