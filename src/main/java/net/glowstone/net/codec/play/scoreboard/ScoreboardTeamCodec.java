package net.glowstone.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage.Action;
import org.bukkit.ChatColor;

public final class ScoreboardTeamCodec implements Codec<ScoreboardTeamMessage> {

    @Override
    public ScoreboardTeamMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode ScoreboardTeamMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ScoreboardTeamMessage message) throws IOException {
        Action action = message.getAction();

        ByteBufUtils.writeUTF8(buf, message.getTeamName());
        buf.writeByte(action.ordinal());

        // CREATE and UPDATE
        if (action == Action.CREATE || action == Action.UPDATE) {
            ByteBufUtils.writeUTF8(buf, message.getDisplayName());
            ByteBufUtils.writeUTF8(buf, message.getPrefix());
            ByteBufUtils.writeUTF8(buf, message.getSuffix());
            buf.writeByte(message.getFlags());
            ByteBufUtils.writeUTF8(buf, message.getNametagVisibility().name().toLowerCase());
            ByteBufUtils.writeUTF8(buf, message.getCollisionRule().name().toLowerCase());
            buf.writeByte(
                message.getColor() == ChatColor.RESET ? -1 : message.getColor().ordinal());
        }

        // CREATE, ADD_, and REMOVE_PLAYERS
        if (action == Action.CREATE || action == Action.ADD_PLAYERS
            || action == Action.REMOVE_PLAYERS) {
            List<String> entries = message.getEntries();
            ByteBufUtils.writeVarInt(buf, entries.size());
            for (String entry : entries) {
                ByteBufUtils.writeUTF8(buf, entry);
            }
        }

        return buf;
    }
}
