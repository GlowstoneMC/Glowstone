package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.CreativeItemPacket;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class CreativeItemCodec implements Codec<CreativeItemPacket> {
    @Override
    public CreativeItemPacket decode(ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        ItemStack item = GlowBufUtils.readSlot(buf, true);
        return new CreativeItemPacket(slot, item);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CreativeItemPacket message) throws IOException {
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
