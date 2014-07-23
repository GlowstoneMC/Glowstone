package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;

import java.io.IOException;

public final class ScoreboardScoreCodec implements Codec<ScoreboardScoreMessage> {
    public ScoreboardScoreMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardScoreMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardScoreMessage message) throws IOException {
        final boolean remove = message.isRemove();
        ByteBufUtils.writeUTF8(buf, message.getTarget());
        buf.writeBoolean(remove);
        if (!remove) {
            ByteBufUtils.writeUTF8(buf, message.getObjective());
            buf.writeInt(message.getValue());
        }
        return buf;
    }
}
