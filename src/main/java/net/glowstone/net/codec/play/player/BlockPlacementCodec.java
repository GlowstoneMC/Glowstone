package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BlockPlacePacket;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockPlacementCodec implements Codec<BlockPlacePacket> {
    @Override
    public BlockPlacePacket decode(ByteBuf buf) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int direction = buf.readByte();
        int hand = ByteBufUtils.readVarInt(buf);
        int cursorX = buf.readByte();
        int cursorY = buf.readByte();
        int cursorZ = buf.readByte();
        return new BlockPlacePacket(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), direction, hand, cursorX, cursorY, cursorZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockPlacePacket message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getDirection());
        ByteBufUtils.writeVarInt(buf, message.getHand());
        buf.writeByte(message.getCursorX());
        buf.writeByte(message.getCursorY());
        buf.writeByte(message.getCursorZ());
        return buf;
    }
}
