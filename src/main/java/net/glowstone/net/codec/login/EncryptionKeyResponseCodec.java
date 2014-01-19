package net.glowstone.net.codec.login;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;

import java.io.IOException;

public final class EncryptionKeyResponseCodec implements Codec<EncryptionKeyResponseMessage> {

    @Override
    public EncryptionKeyResponseMessage decode(ByteBuf buffer) throws IOException {
        byte[] sharedSecret = new byte[buffer.readShort()];
        buffer.readBytes(sharedSecret);

        byte[] verifyToken = new byte[buffer.readShort()];
        buffer.readBytes(verifyToken);

        return new EncryptionKeyResponseMessage(sharedSecret, verifyToken);
    }

    @Override
    public void encode(ByteBuf buf, EncryptionKeyResponseMessage message) throws IOException {
        buf.writeShort(message.getSharedSecret().length);
        buf.writeBytes(message.getSharedSecret());

        buf.writeShort(message.getVerifyToken().length);
        buf.writeBytes(message.getVerifyToken());


    }
}
