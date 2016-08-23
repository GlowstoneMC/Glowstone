package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardScorePacket;

import java.io.IOException;

public final class ScoreboardScoreCodec implements Codec<ScoreboardScorePacket> {
    public ScoreboardScorePacket decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardScoreMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardScorePacket message) throws IOException {
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
