package net.glowstone.net.codec.login;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;

import java.io.IOException;

public final class EncryptionKeyRequestCodec implements Codec<EncryptionKeyRequestMessage> {
    @Override
    public EncryptionKeyRequestMessage decode(ByteBuf buffer) throws IOException {
        String sessionId = ByteBufUtils.readUTF8(buffer);

        byte[] publicKey = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(publicKey);

        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(publicKey);

        return new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EncryptionKeyRequestMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getSessionId());

        ByteBufUtils.writeVarInt(buf, message.getPublicKey().length);
        buf.writeBytes(message.getPublicKey());

        ByteBufUtils.writeVarInt(buf, message.getVerifyToken().length);
        buf.writeBytes(message.getVerifyToken());
        return buf;
    }
}
