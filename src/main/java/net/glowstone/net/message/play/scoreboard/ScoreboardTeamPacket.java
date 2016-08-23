package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.List;

@Data
public final class ScoreboardTeamPacket implements Message {

    private final String teamName;
    private final Action action;

    // CREATE and METADATA only
    private final String displayName;
    private final String prefix;
    private final String suffix;
    private final int flags;
    private final Team.OptionStatus nametagVisibility;
    private final ChatColor color;
    private final Team.OptionStatus collisionRule;

    // CREATE, ADD_, and REMOVE_PLAYERS only
    private final List<String> entries;

    private ScoreboardTeamPacket(String teamName, Action action, String displayName, String prefix, String suffix, boolean friendlyFire, boolean seeInvisible, Team.OptionStatus nametagVisibility, Team.OptionStatus collisionRule, ChatColor color, List<String> entries) {
        this.teamName = teamName;
        this.action = action;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        flags = (friendlyFire ? 1 : 0) | (seeInvisible ? 2 : 0);
        this.nametagVisibility = nametagVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.entries = entries;
    }

    public static ScoreboardTeamPacket create(String teamName, String displayName, String prefix, String suffix, boolean friendlyFire, boolean seeInvisible, Team.OptionStatus nametagVisibility, Team.OptionStatus collisionRule, ChatColor color, List<String> players) {
        return new ScoreboardTeamPacket(teamName, Action.CREATE, displayName, prefix, suffix, friendlyFire, seeInvisible, nametagVisibility, collisionRule, color, players);
    }

    public static ScoreboardTeamPacket remove(String teamName) {
        return new ScoreboardTeamPacket(teamName, Action.REMOVE, null, null, null, false, false, null, null, ChatColor.RESET, null);
    }

    public static ScoreboardTeamPacket update(String teamName, String displayName, String prefix, String suffix, boolean friendlyFire, boolean seeInvisible, Team.OptionStatus nametagVisibility, Team.OptionStatus collisionRule, ChatColor color) {
        return new ScoreboardTeamPacket(teamName, Action.UPDATE, displayName, prefix, suffix, friendlyFire, seeInvisible, nametagVisibility, collisionRule, color, null);
    }

    public static ScoreboardTeamPacket addPlayers(String teamName, List<String> entries) {
        return new ScoreboardTeamPacket(teamName, Action.ADD_PLAYERS, null, null, null, false, false, null, null, ChatColor.RESET, entries);
    }

    public static ScoreboardTeamPacket removePlayers(String teamName, List<String> entries) {
        return new ScoreboardTeamPacket(teamName, Action.REMOVE_PLAYERS, null, null, null, false, false, null, null, ChatColor.RESET, entries);
    }

    public enum Action {
        CREATE,
        REMOVE,
        UPDATE,
        ADD_PLAYERS,
        REMOVE_PLAYERS
    }
}
