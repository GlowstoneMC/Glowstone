package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.UseBedMessage;
import org.bukkit.util.BlockVector;

public final class UseBedCodec implements Codec<UseBedMessage> {

    @Override
    public UseBedMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        return new UseBedMessage(id, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, UseBedMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        return buf;
    }
}
