package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.DiggingMessage;
import org.bukkit.util.BlockVector;

public final class DiggingCodec implements Codec<DiggingMessage> {

    @Override
    public DiggingMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int state = buf.readByte();
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        int face = buf.readByte();
        return new DiggingMessage(state, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), face);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, DiggingMessage message) throws IOException {
        buf.writeByte(message.getState());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFace());
        return buf;
    }
}
