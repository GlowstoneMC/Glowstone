package net.glowstone.scoreboard;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for scoreboard teams.
 */
public final class GlowTeam implements Team {

    private GlowScoreboard scoreboard;
    private final String name;

    private final HashSet<OfflinePlayer> players = new HashSet<>();

    // properties
    private String displayName;
    private String prefix = "";
    private String suffix = "";
    private boolean friendlyFire = false;
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
        Validate.notNull(displayName, "Display name cannot be null");
        checkValid();
        this.displayName = displayName;
    }

    public String getPrefix() throws IllegalStateException {
        checkValid();
        return prefix;
    }

    public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(prefix, "Prefix cannot be null");
        checkValid();
        this.prefix = prefix;
    }

    public String getSuffix() throws IllegalStateException {
        checkValid();
        return suffix;
    }

    public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(suffix, "Suffix cannot be null");
        checkValid();
        this.suffix = suffix;
    }

    public boolean allowFriendlyFire() throws IllegalStateException {
        checkValid();
        return friendlyFire;
    }

    public void setAllowFriendlyFire(boolean enabled) throws IllegalStateException {
        checkValid();
        friendlyFire = enabled;
    }

    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        checkValid();
        return seeInvisible;
    }

    public void setCanSeeFriendlyInvisibles(boolean enabled) throws IllegalStateException {
        checkValid();
        seeInvisible = enabled;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management

    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        checkValid();
        return ImmutableSet.copyOf(players);
    }

    public int getSize() throws IllegalStateException {
        checkValid();
        return players.size();
    }

    public void addPlayer(OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        checkValid();
        players.add(player);
        scoreboard.setPlayerTeam(player, this);
    }

    public boolean removePlayer(OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        checkValid();
        if (players.remove(player)) {
            scoreboard.setPlayerTeam(player, null);
            return true;
        }
        return false;
    }

    public boolean hasPlayer(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(player, "Player cannot be null");
        checkValid();
        return players.contains(player);
    }

    /**
     * Remove a player without propagating the change to the scoreboard.
     * @param player The player to remove.
     */
    void rawRemovePlayer(OfflinePlayer player) {
        players.remove(player);
    }
}
