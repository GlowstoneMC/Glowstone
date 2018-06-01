package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;

public final class SetWindowSlotCodec implements Codec<SetWindowSlotMessage> {

    @Override
    public SetWindowSlotMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode SetWindowSlotMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SetWindowSlotMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
