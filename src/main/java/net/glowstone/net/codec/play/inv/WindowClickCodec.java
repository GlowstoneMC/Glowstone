package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.WindowClickPacket;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class WindowClickCodec implements Codec<WindowClickPacket> {
    @Override
    public WindowClickPacket decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        int slot = buf.readShort();
        int button = buf.readByte();
        int action = buf.readShort();
        int mode = buf.readByte();
        ItemStack item = GlowBufUtils.readSlot(buf, true);
        return new WindowClickPacket(id, slot, button, action, mode, item);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WindowClickPacket message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getSlot());
        buf.writeByte(message.getButton());
        buf.writeShort(message.getTransaction());
        buf.writeByte(message.getMode());
        GlowBufUtils.writeSlot(buf, message.getItem());
        return buf;
    }
}
