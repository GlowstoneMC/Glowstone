package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.TransactionMessage;

import java.io.IOException;

public final class TransactionCodec implements Codec<TransactionMessage> {
    public TransactionMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        int action = buf.readShort();
        boolean accepted = buf.readBoolean();
        return new TransactionMessage(id, action, accepted);
    }

    public ByteBuf encode(ByteBuf buf, TransactionMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getTransaction());
        buf.writeBoolean(message.isAccepted());
        return buf;
    }
}
