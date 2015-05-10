package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;

import java.io.IOException;

public final class ScoreboardDisplayCodec implements Codec<ScoreboardDisplayMessage> {
    public ScoreboardDisplayMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardDisplayMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardDisplayMessage message) throws IOException {
        buf.writeByte(message.getPosition());
        ByteBufUtils.writeUTF8(buf, message.getObjective());
        return buf;
    }
}
