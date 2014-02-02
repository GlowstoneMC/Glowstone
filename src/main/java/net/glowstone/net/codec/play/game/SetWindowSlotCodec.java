package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.SetWindowSlotMessage;
import net.glowstone.util.TagCompoundUtils;

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
            TagCompoundUtils.writeCompound(buf, message.getNbtData());
        }
        return buf;
    }
}
