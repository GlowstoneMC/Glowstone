package net.glowstone.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.network.Message;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import net.glowstone.constants.GlowDisplaySlot;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import net.glowstone.util.TextMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Scoreboard implementation.
 */
public final class GlowScoreboard implements Scoreboard {

    // Objectives
    private final EnumMap<DisplaySlot, GlowObjective> displaySlots = new EnumMap<>(
        DisplaySlot.class);
    private final HashMap<String, GlowObjective> objectives = new HashMap<>();
    private final HashMap<String, Set<GlowObjective>> criteriaMap = new HashMap<>();

    // Score map - kept up to date by each objective
    private final HashMap<String, Set<GlowScore>> scoreMap = new HashMap<>();

    // Teams
    private final HashMap<String, GlowTeam> teams = new HashMap<>();
    private final HashMap<String, GlowTeam> entryTeams = new HashMap<>();

    // Players who are watching this scoreboard
    private final HashSet<GlowPlayer> players = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Send a player this scoreboard's contents and subscribe them to future changes.
     *
     * @param player The player to subscribe.
     */
    public void subscribe(GlowPlayer player) {
        // send all the setup stuff
        // objectives
        for (GlowObjective objective : objectives.values()) {
            player.getSession().send(
                ScoreboardObjectiveMessage.create(objective.getName(),
                        new TextMessage(objective.getDisplayName())));
        }
        // display slots
        for (DisplaySlot slot : DisplaySlot.values()) {
            GlowObjective objective = displaySlots.get(slot);
            String name = objective != null ? objective.getName() : "";
            player.getSession()
                .send(new ScoreboardDisplayMessage(GlowDisplaySlot.getId(slot), name));
        }
        // scores
        for (Entry<String, Set<GlowScore>> entry : scoreMap.entrySet()) {
            for (GlowScore score : entry.getValue()) {
                player.getSession().send(
                    new ScoreboardScoreMessage(entry.getKey(), score.getObjective().getName(),
                        score.getScore()));
            }
        }
        // teams
        for (GlowTeam team : teams.values()) {
            player.getSession().send(team.getCreateMessage());
        }

        // add to player set
        players.add(player);
    }

    /**
     * Clear the player's scoreboard contents and unsubscribe them from future changes.
     *
     * @param player The player to unsubscribe.
     */
    public void unsubscribe(GlowPlayer player) {
        // remove from player set
        players.remove(player);

        // send all the teardown stuff
        // teams
        for (GlowTeam team : teams.values()) {
            player.getSession().send(ScoreboardTeamMessage.remove(team.getName()));
        }
        // display slots
        for (DisplaySlot slot : DisplaySlot.values()) {
            player.getSession().send(new ScoreboardDisplayMessage(GlowDisplaySlot.getId(slot), ""));
        }
        // objectives
        for (GlowObjective objective : objectives.values()) {
            player.getSession().send(ScoreboardObjectiveMessage.remove(objective.getName()));
        }
    }

    /**
     * Broadcast a scoreboard update to all subscribed players.
     *
     * @param message The message to send.
     */
    void broadcast(Message message) {
        for (GlowPlayer player : players) {
            player.getSession().send(message);
        }
    }

    /**
     * Set the objective displayed in the given slot.
     *
     * @param slot The display slot.
     * @param objective The objective to display there, possibly null.
     */
    void setDisplaySlot(DisplaySlot slot, GlowObjective objective) {
        GlowObjective previous = displaySlots.put(slot, objective);

        // previous objective is no longer in this display slot
        if (previous != null) {
            previous.displaySlot = null;
        }

        // new objective is now in this display slot
        if (objective != null) {
            // update objective's display slot
            broadcast(
                new ScoreboardDisplayMessage(GlowDisplaySlot.getId(slot), objective.getName()));
            objective.displaySlot = slot;
        } else {
            // no objective
            broadcast(new ScoreboardDisplayMessage(GlowDisplaySlot.getId(slot), ""));
        }
    }

    /**
     * Unregister an objective from the scoreboard.
     *
     * @param objective The objective to unregister.
     */
    void removeObjective(GlowObjective objective) {
        if (objective.displaySlot != null) {
            setDisplaySlot(objective.displaySlot, null);
        }

        getForCriteria(objective.getCriteria()).remove(objective);
        objectives.remove(objective.getName());
        broadcast(ScoreboardObjectiveMessage.remove(objective.getName()));
    }

    /**
     * Unregister a team from the scoreboard.
     *
     * @param team The team to unregister.
     */
    void removeTeam(GlowTeam team) {
        team.getEntries().forEach(entryTeams::remove);
        teams.remove(team.getName());
        broadcast(ScoreboardTeamMessage.remove(team.getName()));
    }

    /**
     * Get the internal set of objectives corresponding to a given criteria.
     *
     * @param criteria The criteria to look up.
     * @return The set of objectives.
     */
    Set<GlowObjective> getForCriteria(String criteria) {
        Set<GlowObjective> result = criteriaMap.computeIfAbsent(criteria, k -> new HashSet<>());
        return result;
    }

    /**
     * Get the internal set of scores corresponding to a given entry.
     *
     * @param entry The entry to look up.
     * @return The set of scores.
     */
    Set<GlowScore> getScoresForName(String entry) {
        Set<GlowScore> result = scoreMap.computeIfAbsent(entry, k -> new HashSet<>());
        return result;
    }

    /**
     * Update what team a player is associated with.
     *
     * @param player The player.
     * @param team The team, or null for no team.
     */
    void setPlayerTeam(OfflinePlayer player, GlowTeam team) {
        GlowTeam previous = entryTeams.put(player.getName(), team);
        if (previous != null && previous.hasPlayer(player)) {
            previous.removeEntry(player.getName());
            broadcast(ScoreboardTeamMessage
                .removePlayers(previous.getName(), Arrays.asList(player.getName())));
        }
        if (team != null) {
            broadcast(
                ScoreboardTeamMessage.addPlayers(team.getName(), Arrays.asList(player.getName())));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Objectives

    @Override
    public Objective registerNewObjective(String name, String criteria)
        throws IllegalArgumentException {
        checkNotNull(name, "Name cannot be null");
        checkNotNull(criteria, "Criteria cannot be null");
        checkArgument(!objectives.containsKey(name), "Objective \"" + name + "\" already exists");

        GlowObjective objective = new GlowObjective(this, name, criteria);
        objectives.put(name, objective);
        getForCriteria(criteria).add(objective);

        broadcast(ScoreboardObjectiveMessage
            .create(name, new TextMessage(objective.getDisplayName()), RenderType.INTEGER));

        return objective;
    }

    @Override
    public Objective getObjective(String name) throws IllegalArgumentException {
        return objectives.get(name);
    }

    @Override
    public Objective getObjective(DisplaySlot slot) throws IllegalArgumentException {
        checkNotNull(slot, "Slot cannot be null");
        return displaySlots.get(slot);
    }

    @Override
    public Set<Objective> getObjectivesByCriteria(String criteria) throws IllegalArgumentException {
        return ImmutableSet.copyOf(getForCriteria(criteria));
    }

    @Override
    public Set<Objective> getObjectives() {
        return ImmutableSet.copyOf(objectives.values());
    }

    @Override
    public void clearSlot(DisplaySlot slot) throws IllegalArgumentException {
        checkNotNull(slot, "Slot cannot be null");
        setDisplaySlot(slot, null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Teams

    @Override
    public Team registerNewTeam(String name) throws IllegalArgumentException {
        checkNotNull(name, "Name cannot be null");
        checkArgument(!teams.containsKey(name), "Team \"" + name + "\" already exists");

        GlowTeam team = new GlowTeam(this, name);
        teams.put(name, team);
        broadcast(team.getCreateMessage());
        return team;
    }

    @Override
    public Team getPlayerTeam(OfflinePlayer player) throws IllegalArgumentException {
        checkNotNull(player, "Player cannot be null");
        return entryTeams.get(player.getName());
    }

    @Override
    public Team getEntryTeam(String entry) throws IllegalArgumentException {
        checkNotNull(entry, "entry cannot be null");
        return entryTeams.get(entry);
    }

    @Override
    public Team getTeam(String teamName) throws IllegalArgumentException {
        checkNotNull(teamName, "Team name cannot be null");
        return teams.get(teamName);
    }

    @Override
    public Set<Team> getTeams() {
        return ImmutableSet.copyOf(teams.values());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Scores

    @Override
    public Set<String> getEntries() {
        return ImmutableSet.copyOf(scoreMap.keySet());
    }

    @Override
    public Set<Score> getScores(String entry) throws IllegalArgumentException {
        checkNotNull(entry, "Entry cannot be null");

        Set<GlowScore> scoreSet = scoreMap.get(entry);
        if (scoreSet == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(scoreSet);
        }
    }

    @Override
    public Set<Score> getScores(OfflinePlayer player) throws IllegalArgumentException {
        checkNotNull(player, "Player cannot be null");
        return getScores(player.getName());
    }

    @Override
    public void resetScores(String entry) throws IllegalArgumentException {
        checkNotNull(entry, "Entry cannot be null");

        for (GlowObjective objective : objectives.values()) {
            broadcast(ScoreboardScoreMessage.remove(entry, objective.getName()));
            objective.deleteScore(entry);
        }
        scoreMap.remove(entry);
    }

    @Override
    public void resetScores(OfflinePlayer player) throws IllegalArgumentException {
        checkNotNull(player, "Player cannot be null");
        resetScores(player.getName());
    }

    @Override
    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> result = getEntries().stream().map(Bukkit::getOfflinePlayer)
            .collect(Collectors.toSet());
        return Collections.unmodifiableSet(result);
    }
}
