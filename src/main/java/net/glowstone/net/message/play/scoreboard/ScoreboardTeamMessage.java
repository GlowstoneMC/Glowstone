package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;
import net.glowstone.util.TextMessage;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

@Data
public final class ScoreboardTeamMessage implements Message {

    private final String teamName;
    private final Action action;

    // CREATE and METADATA only
    private final TextMessage displayName;
    private final int flags;
    private final Team.OptionStatus nametagVisibility;
    private final ChatColor color;
    private final Team.OptionStatus collisionRule;
    private final TextMessage prefix;
    private final TextMessage suffix;

    // CREATE, ADD_, and REMOVE_PLAYERS only
    private final List<String> entries;

    private ScoreboardTeamMessage(String teamName, Action action, TextMessage displayName,
                                  TextMessage prefix, TextMessage suffix,
                                  boolean friendlyFire, boolean seeInvisible,
                                  Team.OptionStatus nametagVisibility,
                                  Team.OptionStatus collisionRule,
                                  ChatColor color, List<String> entries) {
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

    public static ScoreboardTeamMessage create(String teamName, TextMessage displayName,
                                               TextMessage prefix, TextMessage suffix,
                                               boolean friendlyFire, boolean seeInvisible,
                                               Team.OptionStatus nametagVisibility,
                                               Team.OptionStatus collisionRule,
                                               ChatColor color, List<String> players) {
        return new ScoreboardTeamMessage(teamName, Action.CREATE, displayName, prefix, suffix,
                friendlyFire, seeInvisible, nametagVisibility, collisionRule, color, players);
    }

    public static ScoreboardTeamMessage remove(String teamName) {
        return new ScoreboardTeamMessage(teamName, Action.REMOVE, null, null, null, false, false,
                null, null, ChatColor.RESET, null);
    }

    public static ScoreboardTeamMessage update(String teamName, TextMessage displayName,
                                               TextMessage prefix, TextMessage suffix,
                                               boolean friendlyFire, boolean seeInvisible,
                                               Team.OptionStatus nametagVisibility,
                                               Team.OptionStatus collisionRule, ChatColor color) {
        return new ScoreboardTeamMessage(teamName, Action.UPDATE, displayName, prefix, suffix,
                friendlyFire, seeInvisible, nametagVisibility, collisionRule, color, null);
    }

    public static ScoreboardTeamMessage addPlayers(String teamName, List<String> entries) {
        return new ScoreboardTeamMessage(teamName, Action.ADD_PLAYERS, null, null, null, false,
                false, null, null, ChatColor.RESET, entries);
    }

    public static ScoreboardTeamMessage removePlayers(String teamName, List<String> entries) {
        return new ScoreboardTeamMessage(teamName, Action.REMOVE_PLAYERS, null, null, null, false,
                false, null, null, ChatColor.RESET, entries);
    }

    public enum Action {
        CREATE,
        REMOVE,
        UPDATE,
        ADD_PLAYERS,
        REMOVE_PLAYERS
    }
}
