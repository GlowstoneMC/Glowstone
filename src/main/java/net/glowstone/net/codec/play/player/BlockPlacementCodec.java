package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockPlacementCodec implements Codec<BlockPlacementMessage> {
    @Override
    public BlockPlacementMessage decode(ByteBuf buf) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int direction = buf.readByte();
        ItemStack heldItem = GlowBufUtils.readSlot(buf);
        int cursorX = buf.readByte();
        int cursorY = buf.readByte();
        int cursorZ = buf.readByte();
        return new BlockPlacementMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), direction, heldItem, cursorX, cursorY, cursorZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockPlacementMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getDirection());
        GlowBufUtils.writeSlot(buf, message.getHeldItem());
        buf.writeByte(message.getCursorX());
        buf.writeByte(message.getCursorY());
        buf.writeByte(message.getCursorZ());
        return buf;
    }
}
