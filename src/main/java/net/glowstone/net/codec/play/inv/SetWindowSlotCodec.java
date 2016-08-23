package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.WindowSlotPacket;

import java.io.IOException;

public final class SetWindowSlotCodec implements Codec<WindowSlotPacket> {
    @Override
    public WindowSlotPacket decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode SetWindowSlotMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WindowSlotPacket message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
