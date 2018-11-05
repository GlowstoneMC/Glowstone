package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.inv.HeldItemMessage;

public final class HeldItemCodec implements Codec<HeldItemMessage> {

    @Override
    public HeldItemMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        return new HeldItemMessage(slot);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, HeldItemMessage message) throws IOException {
        // nb: different than decode!
        buf.writeByte(message.getSlot());
        return buf;
    }
}
