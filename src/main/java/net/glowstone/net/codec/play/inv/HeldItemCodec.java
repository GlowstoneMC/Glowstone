package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.HeldItemMessage;

import java.io.IOException;

public final class HeldItemCodec implements Codec<HeldItemMessage> {
    public HeldItemMessage decode(ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        return new HeldItemMessage(slot);
    }

    public ByteBuf encode(ByteBuf buf, HeldItemMessage message) throws IOException {
        buf.writeShort(message.getSlot());
        return buf;
    }
}
