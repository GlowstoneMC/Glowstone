package net.glowstone.net.message.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public final class EncryptionKeyResponseCodec implements Codec<EncryptionKeyResponseMessage> {

    @Override
    public EncryptionKeyResponseMessage decode(ByteBuf buffer) throws IOException {
        byte[] sharedSecret = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(sharedSecret);
        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(verifyToken);

        return new EncryptionKeyResponseWithVerifyToken(sharedSecret, verifyToken);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EncryptionKeyResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getSharedSecret().length);
        buf.writeBytes(message.getSharedSecret());

        if (message.hasVerifyToken()) {
            EncryptionKeyResponseWithVerifyToken tokenMessage = (EncryptionKeyResponseWithVerifyToken) message;
            ByteBufUtils.writeVarInt(buf, tokenMessage.getVerifyToken().length);
            buf.writeBytes(tokenMessage.getVerifyToken());
        } else {
            EncryptionKeyResponseWithSignature signatureMessage = (EncryptionKeyResponseWithSignature) message;
            buf.writeLong(signatureMessage.getSalt());
            ByteBufUtils.writeVarInt(buf, signatureMessage.getMessageSignature().length);
            buf.writeBytes(signatureMessage.getMessageSignature());
        }

        return buf;
    }
}
