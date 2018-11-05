package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import org.bukkit.inventory.ItemStack;

public final class CreativeItemCodec implements Codec<CreativeItemMessage> {

    @Override
    public CreativeItemMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int slot = buf.readShort();
        ItemStack item = GlowBufUtils.readSlot(buf, true);
        return new CreativeItemMessage(slot, item);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, CreativeItemMessage message) throws IOException {
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
