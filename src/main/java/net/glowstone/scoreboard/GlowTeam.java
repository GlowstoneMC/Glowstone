package net.glowstone.scoreboard;

import com.flowpowered.networking.Message;
import com.google.common.collect.ImmutableSet;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import org.apache.commons.lang3.Validate;
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
    private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
    private NameTagVisibility deathMessageVisibility = NameTagVisibility.ALWAYS;
    private ChatColor color = ChatColor.RESET;
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

    Message getCreateMessage() {
        List<String> playerNames = new ArrayList<>(players.size());
        playerNames.addAll(players.stream().map(OfflinePlayer::getName).collect(Collectors.toList()));
        return ScoreboardTeamMessage.create(name, displayName, prefix, suffix, friendlyFire, seeInvisible, nameTagVisibility, color, playerNames);
    }

    private void update() {
        scoreboard.broadcast(ScoreboardTeamMessage.update(name, displayName, prefix, suffix, friendlyFire, seeInvisible, nameTagVisibility, color));
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
        update();
    }

    public String getPrefix() throws IllegalStateException {
        checkValid();
        return prefix;
    }

    public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(prefix, "Prefix cannot be null");
        checkValid();
        this.prefix = prefix;
        update();
    }

    public String getSuffix() throws IllegalStateException {
        checkValid();
        return suffix;
    }

    public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(suffix, "Suffix cannot be null");
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

    public NameTagVisibility getNameTagVisibility() throws IllegalStateException {
        checkValid();
        return nameTagVisibility;
    }

    public void setNameTagVisibility(NameTagVisibility visibility) throws IllegalStateException {
        checkValid();
        nameTagVisibility = visibility;
        update();
    }

    public NameTagVisibility getDeathMessageVisibility() throws IllegalStateException {
        return deathMessageVisibility;
    }

    @Override
    public boolean hasEntry(String s) throws IllegalArgumentException, IllegalStateException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDeathMessageVisibility(NameTagVisibility deathMessageVisibility) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(deathMessageVisibility, "NameTagVisibility cannot be null!");
        checkValid();
        this.deathMessageVisibility = deathMessageVisibility;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management

    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        checkValid();
        return ImmutableSet.copyOf(players);
    }

    @Override
    public Set<String> getEntries() throws IllegalStateException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    @Override
    public void addEntry(String s) throws IllegalStateException, IllegalArgumentException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) throws IllegalArgumentException {
        if (color.isFormat()) {
            throw new IllegalArgumentException("Formatting codes cannot be used as a team color!");
        }
        this.color = color;
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

    @Override
    public boolean removeEntry(String s) throws IllegalStateException, IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasPlayer(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(player, "Player cannot be null");
        checkValid();
        return players.contains(player);
    }

    public String getPlayerDisplayName(String name) {
        return getPrefix() + name + getSuffix();
    }

    /**
     * Remove a player without propagating the change to the scoreboard.
     * @param player The player to remove.
     */
    void rawRemovePlayer(OfflinePlayer player) {
        players.remove(player);
    }
}
