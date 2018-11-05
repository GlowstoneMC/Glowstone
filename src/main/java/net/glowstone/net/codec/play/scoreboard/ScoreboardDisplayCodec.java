package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;

public final class ScoreboardDisplayCodec implements Codec<ScoreboardDisplayMessage> {

    @Override
    public ScoreboardDisplayMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardDisplayMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ScoreboardDisplayMessage message) throws IOException {
        buf.writeByte(message.getPosition());
        ByteBufUtils.writeUTF8(buf, message.getObjective());
        return buf;
    }
}
