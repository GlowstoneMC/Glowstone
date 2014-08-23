package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.util.ByteBufUtils;
import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockChangeCodec implements Codec<BlockChangeMessage> {
    public BlockChangeMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int type = ByteBufUtils.readVarInt(buffer);

        // todo: this code belongs elsewhere
        int metadata = type & 0xf;
        type >>= 4;

        return new BlockChangeMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), type, metadata);
    }

    public ByteBuf encode(ByteBuf buf, BlockChangeMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());

        // todo: this code belongs elsewhere
        int type = (message.getType() << 4) | message.getMetadata();
        ByteBufUtils.writeVarInt(buf, type);
        return buf;
    }
}
