package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;

public final class EncryptionKeyResponseCodec implements Codec<EncryptionKeyResponseMessage> {

    @Override
    public EncryptionKeyResponseMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        byte[] sharedSecret = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(sharedSecret);

        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(verifyToken);

        return new EncryptionKeyResponseMessage(sharedSecret, verifyToken);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, EncryptionKeyResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getSharedSecret().length);
        buf.writeBytes(message.getSharedSecret());

        ByteBufUtils.writeVarInt(buf, message.getVerifyToken().length);
        buf.writeBytes(message.getVerifyToken());
        return buf;
    }
}
