package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage.Action;

import java.io.IOException;
import java.util.List;

public final class ScoreboardTeamCodec implements Codec<ScoreboardTeamMessage> {
    public ScoreboardTeamMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardTeamMessage");
    }

    public ByteBuf encode(ByteBuf buf, ScoreboardTeamMessage message) throws IOException {
        final Action action = message.getAction();

        ByteBufUtils.writeUTF8(buf, message.getTeamName());
        buf.writeByte(action.ordinal());

        // CREATE and UPDATE
        if (action == Action.CREATE || action == Action.UPDATE) {
            ByteBufUtils.writeUTF8(buf, message.getDisplayName());
            ByteBufUtils.writeUTF8(buf, message.getPrefix());
            ByteBufUtils.writeUTF8(buf, message.getSuffix());
            buf.writeByte(message.getFlags());
        }

        // CREATE, ADD_, and REMOVE_PLAYERS
        if (action == Action.CREATE || action == Action.ADD_PLAYERS || action == Action.REMOVE_PLAYERS) {
            final List<String> entries = message.getEntries();
            buf.writeShort(entries.size());
            for (String entry : entries) {
                ByteBufUtils.writeUTF8(buf, entry);
            }
        }

        return buf;
    }
}
