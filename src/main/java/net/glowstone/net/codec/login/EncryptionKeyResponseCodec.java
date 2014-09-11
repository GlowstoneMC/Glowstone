package net.glowstone.net.codec.login;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;

import java.io.IOException;

public final class EncryptionKeyResponseCodec implements Codec<EncryptionKeyResponseMessage> {
    @Override
    public EncryptionKeyResponseMessage decode(ByteBuf buffer) throws IOException {
        byte[] sharedSecret = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(sharedSecret);

        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(verifyToken);

        return new EncryptionKeyResponseMessage(sharedSecret, verifyToken);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EncryptionKeyResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getSharedSecret().length);
        buf.writeBytes(message.getSharedSecret());

        ByteBufUtils.writeVarInt(buf, message.getVerifyToken().length);
        buf.writeBytes(message.getVerifyToken());
        return buf;
    }
}
