package net.glowstone.scoreboard;

import com.flowpowered.network.Message;
import com.google.common.collect.ImmutableSet;
import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation for scoreboard teams.
 */
public final class GlowTeam implements Team {

    private final String name;
    private final HashSet<String> players = new HashSet<>();
    private GlowScoreboard scoreboard;
    // properties
    private String displayName;
    private String prefix = "";
    private String suffix = "";
    private Team.OptionStatus nameTagVisibility = Team.OptionStatus.ALWAYS;
    private Team.OptionStatus deathMessageVisibility = Team.OptionStatus.ALWAYS;
    private Team.OptionStatus collisionRule = Team.OptionStatus.ALWAYS;
    private ChatColor color = ChatColor.RESET;
    private boolean friendlyFire;
    private boolean seeInvisible = true;

    public GlowTeam(GlowScoreboard scoreboard, String name) {
        this.scoreboard = scoreboard;
        this.name = name;
        displayName = name;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void unregister() throws IllegalStateException {
        checkValid();
        scoreboard.removeTeam(this);
        scoreboard = null;
    }

    void checkValid() {
        if (scoreboard == null) {
            throw new IllegalStateException("Cannot manipulate unregistered team");
        }
    }

    Message getCreateMessage() {
        List<String> playerNames = new ArrayList<>(players.size());
        playerNames.addAll(players);
        return ScoreboardTeamMessage.create(name, displayName, prefix, suffix, friendlyFire, seeInvisible, nameTagVisibility, collisionRule, color, playerNames);
    }

    private void update() {
        scoreboard.broadcast(ScoreboardTeamMessage.update(name, displayName, prefix, suffix, friendlyFire, seeInvisible, nameTagVisibility, collisionRule, color));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    public String getName() throws IllegalStateException {
        checkValid();
        return name;
    }

    public String getDisplayName() throws IllegalStateException {
        checkValid();
        return displayName;
    }

    public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(displayName, "Display name cannot be null");
        checkValid();
        this.displayName = displayName;
        update();
    }

    public String getPrefix() throws IllegalStateException {
        checkValid();
        return prefix;
    }

    public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(prefix, "Prefix cannot be null");
        checkValid();
        this.prefix = prefix;
        update();
    }

    public String getSuffix() throws IllegalStateException {
        checkValid();
        return suffix;
    }

    public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(suffix, "Suffix cannot be null");
        checkValid();
        this.suffix = suffix;
        update();
    }

    public boolean allowFriendlyFire() throws IllegalStateException {
        checkValid();
        return friendlyFire;
    }

    public void setAllowFriendlyFire(boolean enabled) throws IllegalStateException {
        checkValid();
        friendlyFire = enabled;
        update();
    }

    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        checkValid();
        return seeInvisible;
    }

    public void setCanSeeFriendlyInvisibles(boolean enabled) throws IllegalStateException {
        checkValid();
        seeInvisible = enabled;
        update();
    }

    @Deprecated
    public NameTagVisibility getNameTagVisibility() throws IllegalStateException {
        checkValid();
        return NameTagVisibility.valueOf(nameTagVisibility.name());
    }

    @Deprecated
    public void setNameTagVisibility(NameTagVisibility visibility) throws IllegalStateException {
        checkValid();
        nameTagVisibility = OptionStatus.valueOf(visibility.name());
        update();
    }

    @Deprecated
    public NameTagVisibility getDeathMessageVisibility() throws IllegalStateException {
        checkValid();
        return NameTagVisibility.valueOf(deathMessageVisibility.name());
    }

    @Deprecated
    public void setDeathMessageVisibility(NameTagVisibility deathMessageVisibility) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(deathMessageVisibility, "NameTagVisibility cannot be null!");
        checkValid();
        this.deathMessageVisibility = OptionStatus.valueOf(deathMessageVisibility.name());
    }

    @Override
    public OptionStatus getOption(Option option) throws IllegalStateException {
        checkValid();
        if (option == Option.DEATH_MESSAGE_VISIBILITY) {
            return deathMessageVisibility;
        } else if (option == Option.NAME_TAG_VISIBILITY) {
            return nameTagVisibility;
        } else if (option == Option.COLLISION_RULE) {
            return collisionRule;
        } else {
            return null;
        }
    }

    @Override
    public void setOption(Option option, OptionStatus status) throws IllegalStateException {
        checkValid();
        if (option == Option.DEATH_MESSAGE_VISIBILITY) {
            deathMessageVisibility = status;
        } else if (option == Option.NAME_TAG_VISIBILITY) {
            nameTagVisibility = status;
        } else if (option == Option.COLLISION_RULE) {
            collisionRule = status;
        }
        update();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management
    @Override
    public Set<String> getEntries() throws IllegalStateException {
        checkValid();
        return ImmutableSet.copyOf(players);
    }

    @Override
    @Deprecated
    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        Set<OfflinePlayer> playerObjectSet = new HashSet<>(players.size());
        playerObjectSet.addAll(players.stream().map(s -> new GlowOfflinePlayer((GlowServer) Bukkit.getServer(), s)).collect(Collectors.toList()));
        return playerObjectSet;
    }


    @Override
    public boolean hasEntry(String s) throws IllegalArgumentException, IllegalStateException {
        checkValid();
        return players.contains(s);
    }

    @Override
    @Deprecated
    public boolean hasPlayer(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        return players.contains(player.getName());
    }

    @Override
    public int getSize() throws IllegalStateException {
        checkValid();
        return players.size();
    }

    @Override
    public void addEntry(String s) throws IllegalStateException, IllegalArgumentException {
        checkValid();
        players.add(s);
    }

    @Override
    @Deprecated
    public void addPlayer(OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        players.add(player.getName());
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public void setColor(ChatColor color) throws IllegalArgumentException {
        if (color.isFormat()) {
            throw new IllegalArgumentException("Formatting codes cannot be used as a team color!");
        }
        this.color = color;
    }

    @Override
    public boolean removeEntry(String s) throws IllegalStateException, IllegalArgumentException {
        checkValid();
        return players.remove(s);
    }

    @Override
    @Deprecated
    public boolean removePlayer(OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        return players.remove(player.getName());
    }

    public String getPlayerDisplayName(String name) {
        return getPrefix() + name + getSuffix();
    }
}
