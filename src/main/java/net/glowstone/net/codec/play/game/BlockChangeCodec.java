package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import org.bukkit.util.BlockVector;

public final class BlockChangeCodec implements Codec<BlockChangeMessage> {

    @Override
    public BlockChangeMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int type = ByteBufUtils.readVarInt(buffer);
        return new BlockChangeMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), type);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, BlockChangeMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        ByteBufUtils.writeVarInt(buf, message.getType());
        return buf;
    }
}
