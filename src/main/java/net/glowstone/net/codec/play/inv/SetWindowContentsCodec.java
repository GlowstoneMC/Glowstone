package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.SetWindowContentsMessage;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class SetWindowContentsCodec implements Codec<SetWindowContentsMessage> {
    @Override
    public SetWindowContentsMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SetWindowContentsMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SetWindowContentsMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getItems().length);
        for (ItemStack item : message.getItems()) {
            GlowBufUtils.writeSlot(buf, item);
        }
        return buf;
    }
}
