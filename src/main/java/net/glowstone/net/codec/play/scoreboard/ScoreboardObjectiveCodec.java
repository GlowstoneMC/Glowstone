package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;

import java.io.IOException;

public final class ScoreboardObjectiveCodec implements Codec<ScoreboardObjectiveMessage> {

    @Override
    public ScoreboardObjectiveMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardObjectiveMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ScoreboardObjectiveMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getName());
        buf.writeByte(message.getAction());

        boolean hasDisplayName = message.getDisplayName() != null;
        buf.writeBoolean(hasDisplayName);
        if (hasDisplayName) {
            GlowBufUtils.writeChat(buf, message.getDisplayName());
        }

        boolean hasRenderType = message.getRenderType() != null;
        buf.writeBoolean(hasRenderType);
        if (hasRenderType) {
            ByteBufUtils.writeVarInt(buf, message.getRenderType().ordinal());
        }
        return buf;
    }
}
