package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;

import java.io.IOException;

public final class ScoreboardObjectiveCodec implements Codec<ScoreboardObjectiveMessage> {
    public ScoreboardObjectiveMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardObjectiveMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardObjectiveMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getName());
        ByteBufUtils.writeUTF8(buf, message.getDisplayName());
        buf.writeByte(message.getAction());
        return buf;
    }
}
