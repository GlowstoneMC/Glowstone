package net.glowstone.net.codec.login;

import com.flowpowered.networking.util.ByteBufUtils;
import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;

import java.io.IOException;

public final class EncryptionKeyRequestCodec implements Codec<EncryptionKeyRequestMessage> {
    @Override
    public EncryptionKeyRequestMessage decode(ByteBuf buffer) throws IOException {
        String sessionId = ByteBufUtils.readUTF8(buffer);

        int publicKeyLength = buffer.readShort();
        byte[] publicKey = new byte[publicKeyLength];
        buffer.readBytes(publicKey);

        int verifyTokenLength = buffer.readShort();
        byte[] verifyToken = new byte[verifyTokenLength];
        buffer.readBytes(publicKey);

        return new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken);
    }

    @Override
    public void encode(ByteBuf buf, EncryptionKeyRequestMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getSessionId());

        buf.writeShort(message.getPublicKey().length);
        buf.writeBytes(message.getPublicKey());

        buf.writeShort(message.getVerifyToken().length);
        buf.writeBytes(message.getVerifyToken());
    }
}
