package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BlockDigPacket;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class DiggingCodec implements Codec<BlockDigPacket> {
    @Override
    public BlockDigPacket decode(ByteBuf buf) throws IOException {
        int state = buf.readByte();
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int face = buf.readByte();
        return new BlockDigPacket(state, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), face);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockDigPacket message) throws IOException {
        buf.writeByte(message.getState());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFace());
        return buf;
    }
}
