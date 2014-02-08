package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.SetWindowSlotMessage;

import java.io.IOException;

public final class SetWindowSlotCodec implements Codec<SetWindowSlotMessage> {
    public SetWindowSlotMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode SetWindowSlotMessage");
    }

    public ByteBuf encode(ByteBuf buf, SetWindowSlotMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getSlot());
        buf.writeShort(message.getItem());
        if (message.getItem() != -1) {
            buf.writeByte(message.getCount());
            buf.writeShort(message.getDamage());
            GlowBufUtils.writeCompound(buf, message.getNbtData());
        }
        return buf;
    }
}
