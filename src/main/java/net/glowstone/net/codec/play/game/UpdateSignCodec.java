package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateSignPacket;
import net.glowstone.util.TextMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class UpdateSignCodec implements Codec<UpdateSignPacket> {
    @Override
    public UpdateSignPacket decode(ByteBuf buf) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        TextMessage[] message = new TextMessage[4];
        for (int i = 0; i < message.length; ++i) {
            message[i] = GlowBufUtils.readChat(buf);
        }
        return new UpdateSignPacket(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), message);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UpdateSignPacket message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        for (TextMessage line : message.getMessage()) {
            GlowBufUtils.writeChat(buf, line);
        }
        return buf;
    }
}
