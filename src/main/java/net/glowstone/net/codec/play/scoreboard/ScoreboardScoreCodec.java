package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;

public final class ScoreboardScoreCodec implements Codec<ScoreboardScoreMessage> {

    @Override
    public ScoreboardScoreMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardScoreMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ScoreboardScoreMessage message) throws IOException {
        boolean remove = message.isRemove();
        ByteBufUtils.writeUTF8(buf, message.getTarget());
        buf.writeByte(remove ? 1 : 0);
        ByteBufUtils.writeUTF8(buf, message.getObjective());
        if (!remove) {
            ByteBufUtils.writeVarInt(buf, message.getValue());
        }
        return buf;
    }
}
