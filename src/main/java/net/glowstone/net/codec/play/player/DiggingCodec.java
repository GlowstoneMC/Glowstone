package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.DiggingMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class DiggingCodec implements Codec<DiggingMessage> {
    @Override
    public DiggingMessage decode(ByteBuf buf) throws IOException {
        int state = buf.readByte();
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int face = buf.readByte();
        return new DiggingMessage(state, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), face);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DiggingMessage message) throws IOException {
        buf.writeByte(message.getState());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFace());
        return buf;
    }
}
