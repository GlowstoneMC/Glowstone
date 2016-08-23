package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.TransactionPacket;

import java.io.IOException;

public final class TransactionCodec implements Codec<TransactionPacket> {
    @Override
    public TransactionPacket decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        int action = buf.readShort();
        boolean accepted = buf.readBoolean();
        return new TransactionPacket(id, action, accepted);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TransactionPacket message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getTransaction());
        buf.writeBoolean(message.isAccepted());
        return buf;
    }
}
