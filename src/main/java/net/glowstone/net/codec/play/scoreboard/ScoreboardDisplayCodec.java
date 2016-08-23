package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayPacket;

import java.io.IOException;

public final class ScoreboardDisplayCodec implements Codec<ScoreboardDisplayPacket> {
    public ScoreboardDisplayPacket decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardDisplayMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardDisplayPacket message) throws IOException {
        buf.writeByte(message.getPosition());
        ByteBufUtils.writeUTF8(buf, message.getObjective());
        return buf;
    }
}
