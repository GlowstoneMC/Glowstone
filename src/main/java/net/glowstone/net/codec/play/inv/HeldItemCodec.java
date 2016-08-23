package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.HeldItemPacket;

import java.io.IOException;

public final class HeldItemCodec implements Codec<HeldItemPacket> {
    @Override
    public HeldItemPacket decode(ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        return new HeldItemPacket(slot);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HeldItemPacket message) throws IOException {
        // nb: different than decode!
        buf.writeByte(message.getSlot());
        return buf;
    }
}
