package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;

public final class EncryptionKeyRequestCodec implements Codec<EncryptionKeyRequestMessage> {

    @Override
    public EncryptionKeyRequestMessage decode(ByteBuf buffer) throws IOException {
        String sessionId = ByteBufUtils.readUTF8(buffer);

        byte[] publicKey = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(publicKey);

        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(verifyToken);

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
