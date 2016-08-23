package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockChangePacket;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockChangeCodec implements Codec<BlockChangePacket> {
    @Override
    public BlockChangePacket decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int type = ByteBufUtils.readVarInt(buffer);
        return new BlockChangePacket(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), type);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockChangePacket message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        ByteBufUtils.writeVarInt(buf, message.getType());
        return buf;
    }
}
