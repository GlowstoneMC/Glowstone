package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockPlacementCodec implements Codec<BlockPlacementMessage> {

    @Override
    public BlockPlacementMessage decode(ByteBuf buf) throws IOException {
        int hand = ByteBufUtils.readVarInt(buf);
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int face = ByteBufUtils.readVarInt(buf);

        float cursorX = buf.readFloat();
        float cursorY = buf.readFloat();
        float cursorZ = buf.readFloat();
        boolean isInsideBlock = buf.readBoolean();
        int sequence = ByteBufUtils.readVarInt(buf);
        return new BlockPlacementMessage(hand, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), face, cursorX, cursorY, cursorZ, isInsideBlock, sequence);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockPlacementMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getHand());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        ByteBufUtils.writeVarInt(buf, message.getFace());

        buf.writeFloat(message.getCursorX());
        buf.writeFloat(message.getCursorY());
        buf.writeFloat(message.getCursorZ());
        buf.writeBoolean(message.isInsideBlock());
        ByteBufUtils.writeVarInt(buf, message.getSequence());
        return buf;
    }
}
