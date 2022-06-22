package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.SpawnPositionMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class SpawnPositionCodec implements Codec<SpawnPositionMessage> {

    @Override
    public SpawnPositionMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int angle = buffer.readByte();
        return new SpawnPositionMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), angle);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPositionMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeFloat(message.getAngle());
        return buf;
    }
}
