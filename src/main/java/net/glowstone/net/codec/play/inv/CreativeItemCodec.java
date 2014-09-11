package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class CreativeItemCodec implements Codec<CreativeItemMessage> {
    @Override
    public CreativeItemMessage decode(ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        ItemStack item = GlowBufUtils.readSlot(buf);
        return new CreativeItemMessage(slot, item);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CreativeItemMessage message) throws IOException {
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
