package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.inv.TransactionMessage;

public final class TransactionCodec implements Codec<TransactionMessage> {

    @Override
    public TransactionMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        int action = buf.readShort();
        boolean accepted = buf.readBoolean();
        return new TransactionMessage(id, action, accepted);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TransactionMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getTransaction());
        buf.writeBoolean(message.isAccepted());
        return buf;
    }
}
