package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;

public final class ScoreboardObjectiveCodec implements Codec<ScoreboardObjectiveMessage> {

    @Override
    public ScoreboardObjectiveMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardObjectiveMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ScoreboardObjectiveMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getName());
        buf.writeByte(message.getAction());
        if (message.getDisplayName() != null) {
            ByteBufUtils.writeUTF8(buf, message.getDisplayName());
        }
        if (message.getRenderType() != null) {
            ByteBufUtils.writeUTF8(buf, message.getRenderType().name());
        }
        return buf;
    }
}
