package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ServerDifficultyMessage;
import org.bukkit.Difficulty;

import java.io.IOException;

public final class ServerDifficultyCodec implements Codec<ServerDifficultyMessage> {

    @Override
    public ServerDifficultyMessage decode(ByteBuf buffer) throws IOException {
        int difficulty = buffer.readUnsignedByte();
        return new ServerDifficultyMessage(Difficulty.values()[difficulty]);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ServerDifficultyMessage message) throws IOException {
        return buf.writeByte(message.getDifficulty().ordinal());
    }
}
