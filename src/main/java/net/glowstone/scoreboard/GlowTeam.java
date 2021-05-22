package net.glowstone.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.network.Message;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import net.glowstone.util.TextMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation for scoreboard teams.
 */
public final class GlowTeam implements Team {

    private final String name;
    private final HashSet<String> players = new HashSet<>();
    @Getter
    private GlowScoreboard scoreboard;
    // properties
    private String displayName;
    private String prefix = "";
    private String suffix = "";
    private Team.OptionStatus nameTagVisibility = Team.OptionStatus.ALWAYS;
    private Team.OptionStatus deathMessageVisibility = Team.OptionStatus.ALWAYS;
    private Team.OptionStatus collisionRule = Team.OptionStatus.ALWAYS;
    @Getter
    private ChatColor color = ChatColor.RESET;
    private boolean friendlyFire;
    private boolean seeInvisible = true;

    /**
     * Creates a team.
     *
     * @param scoreboard the scoreboard for this team's scores
     * @param name the team name
     */
    public GlowTeam(GlowScoreboard scoreboard, String name) {
        this.scoreboard = scoreboard;
        this.name = name;
        displayName = name;
    }

    @Override
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
        return ScoreboardTeamMessage
            .create(name, new TextMessage(displayName), new TextMessage(prefix),
                    new TextMessage(suffix), friendlyFire, seeInvisible, nameTagVisibility,
                    collisionRule, color, playerNames);
    }

    private void update() {
        scoreboard.broadcast(ScoreboardTeamMessage
            .update(name, new TextMessage(displayName), new TextMessage(prefix),
                    new TextMessage(suffix), friendlyFire, seeInvisible, nameTagVisibility,
                    collisionRule, color));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    @Override
    public String getName() throws IllegalStateException {
        checkValid();
        return name;
    }

    @Override
    public @NotNull Component displayName() throws IllegalStateException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public void displayName(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public @NotNull Component prefix() throws IllegalStateException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public void prefix(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public @NotNull Component suffix() throws IllegalStateException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public void suffix(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public @NotNull TextColor color() throws IllegalStateException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public void color(@Nullable NamedTextColor namedTextColor) {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public String getDisplayName() throws IllegalStateException {
        checkValid();
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName)
        throws IllegalStateException, IllegalArgumentException {
        checkNotNull(displayName, "Display name cannot be null");
        checkValid();
        this.displayName = displayName;
        update();
    }

    @Override
    public String getPrefix() throws IllegalStateException {
        checkValid();
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(prefix, "Prefix cannot be null");
        checkValid();
        this.prefix = prefix;
        update();
    }

    @Override
    public String getSuffix() throws IllegalStateException {
        checkValid();
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) throws IllegalStateException, IllegalArgumentException {
        checkNotNull(suffix, "Suffix cannot be null");
        checkValid();
        this.suffix = suffix;
        update();
    }

    @Override
    public boolean allowFriendlyFire() throws IllegalStateException {
        checkValid();
        return friendlyFire;
    }

    @Override
    public void setAllowFriendlyFire(boolean enabled) throws IllegalStateException {
        checkValid();
        friendlyFire = enabled;
        update();
    }

    @Override
    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        checkValid();
        return seeInvisible;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean enabled) throws IllegalStateException {
        checkValid();
        seeInvisible = enabled;
        update();
    }

    @Override
    public NameTagVisibility getNameTagVisibility() throws IllegalStateException {
        checkValid();
        return NameTagVisibility.valueOf(nameTagVisibility.name());
    }

    @Override
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

    /**
     * Sets to whom death messages are visible.
     *
     * @param deathMessageVisibility the new death message visibility
     * @throws IllegalStateException if this team is not registered with a scoreboard
     * @throws IllegalArgumentException if {@code deathMessageVisibility} is null
     */
    @Deprecated
    public void setDeathMessageVisibility(NameTagVisibility deathMessageVisibility)
        throws IllegalStateException, IllegalArgumentException {
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
        playerObjectSet.addAll(
            players.stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList()));
        return playerObjectSet;
    }


    @Override
    public boolean hasEntry(String s) throws IllegalArgumentException, IllegalStateException {
        checkValid();
        return players.contains(s);
    }

    @Override
    @Deprecated
    public boolean hasPlayer(OfflinePlayer player)
        throws IllegalArgumentException, IllegalStateException {
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
    public void addPlayer(OfflinePlayer player)
        throws IllegalStateException, IllegalArgumentException {
        players.add(player.getName());
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
    public boolean removePlayer(OfflinePlayer player)
        throws IllegalStateException, IllegalArgumentException {
        return players.remove(player.getName());
    }

    public String getPlayerDisplayName(String name) {
        return getPrefix() + name + getSuffix();
    }
}
