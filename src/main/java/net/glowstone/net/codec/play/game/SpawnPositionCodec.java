package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.SpawnPositionMessage;
import org.bukkit.util.BlockVector;

public final class SpawnPositionCodec implements Codec<SpawnPositionMessage> {

    @Override
    public SpawnPositionMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        return new SpawnPositionMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPositionMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        return buf;
    }
}
