package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ServerDifficultyPacket;

import java.io.IOException;

public final class ServerDifficultyCodec implements Codec<ServerDifficultyPacket> {

    @Override
    public ServerDifficultyPacket decode(ByteBuf buffer) throws IOException {
        int difficulty = buffer.readUnsignedByte();
        return new ServerDifficultyPacket(difficulty);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ServerDifficultyPacket message) throws IOException {
        return buf.writeByte(message.getDifficulty());
    }
}
