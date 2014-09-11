package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class UpdateSignCodec implements Codec<UpdateSignMessage> {
    @Override
    public UpdateSignMessage decode(ByteBuf buf) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        String[] message = new String[4];
        for (int i = 0; i < message.length; ++i) {
            message[i] = ByteBufUtils.readUTF8(buf);
        }
        return new UpdateSignMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), message);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UpdateSignMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        for (String line : message.getMessage()) {
            ByteBufUtils.writeUTF8(buf, line);
        }
        return buf;
    }
}
