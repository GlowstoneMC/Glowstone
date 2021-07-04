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
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int direction = buf.readByte();
        int hand = ByteBufUtils.readVarInt(buf);
        float cursorX = buf.readFloat();
        float cursorY = buf.readFloat();
        float cursorZ = buf.readFloat();
        return new BlockPlacementMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
            direction, hand, cursorX, cursorY, cursorZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockPlacementMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getDirection());
        ByteBufUtils.writeVarInt(buf, message.getHand());
        buf.writeFloat(message.getCursorX());
        buf.writeFloat(message.getCursorY());
        buf.writeFloat(message.getCursorZ());
        return buf;
    }
}
