package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class BlockPlacementCodec implements Codec<BlockPlacementMessage> {
    public BlockPlacementMessage decode(ByteBuf buf) throws IOException {
        int x = buf.readInt();
        int y = buf.readUnsignedByte();
        int z = buf.readInt();
        int direction = buf.readByte();
        ItemStack heldItem = GlowBufUtils.readSlot(buf);
        int cursorX = buf.readByte();
        int cursorY = buf.readByte();
        int cursorZ = buf.readByte();
        return new BlockPlacementMessage(x, y, z, direction, heldItem, cursorX, cursorY, cursorZ);
    }

    public ByteBuf encode(ByteBuf buf, BlockPlacementMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeByte(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getDirection());
        GlowBufUtils.writeSlot(buf, message.getHeldItem());
        buf.writeByte(message.getCursorX());
        buf.writeByte(message.getCursorY());
        buf.writeByte(message.getCursorZ());
        return buf;
    }
}
