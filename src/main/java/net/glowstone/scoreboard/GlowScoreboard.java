package net.glowstone.scoreboard;

import com.google.common.collect.ImmutableSet;
import net.glowstone.GlowServer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;

import java.util.*;

/**
 * Scoreboard implementation.
 */
public final class GlowScoreboard implements Scoreboard {

    private final GlowServer server;

    // Objectives
    private final EnumMap<DisplaySlot, GlowObjective> displaySlots = new EnumMap<>(DisplaySlot.class);
    private final HashMap<String, GlowObjective> objectives = new HashMap<>();
    private final HashMap<String, Set<GlowObjective>> criteriaMap = new HashMap<>();

    // Score map - kept up to date by each objective
    private final HashMap<String, Set<GlowScore>> scoreMap = new HashMap<>();

    public GlowScoreboard(GlowServer server) {
        this.server = server;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    void setDisplaySlot(DisplaySlot slot, GlowObjective objective) {
        GlowObjective previous = displaySlots.put(slot, objective);

        if (previous != null) {
            previous.displaySlot = null;
        }

        if (objective != null) {
            objective.displaySlot = slot;
        }
    }

    void remove(GlowObjective objective) {
        if (objective.displaySlot != null) {
            setDisplaySlot(objective.displaySlot, null);
        }

        getForCriteria(objective.getCriteria()).remove(objective);
    }

    Set<GlowObjective> getForCriteria(String criteria) {
        Set<GlowObjective> result = criteriaMap.get(criteria);
        if (result == null) {
            result = new HashSet<>();
            criteriaMap.put(criteria, result);
        }
        return result;
    }

    Set<GlowScore> getScoresForName(String entry) {
        Set<GlowScore> result = scoreMap.get(entry);
        if (result == null) {
            result = new HashSet<>();
            scoreMap.put(entry, result);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Objectives

    public Objective registerNewObjective(String name, String criteria) throws IllegalArgumentException {
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(criteria, "Criteria cannot be null");
        Validate.isTrue(!objectives.containsKey(name), "Objective \"" + name + "\" already exists");

        GlowObjective objective = new GlowObjective(this, name, criteria);
        objectives.put(name, objective);
        getForCriteria(criteria).add(objective);

        return objective;
    }

    public Objective getObjective(String name) throws IllegalArgumentException {
        return objectives.get(name);
    }

    public Set<Objective> getObjectivesByCriteria(String criteria) throws IllegalArgumentException {
        return ImmutableSet.<Objective>copyOf(getForCriteria(criteria));
    }

    public Set<Objective> getObjectives() {
        return ImmutableSet.<Objective>copyOf(objectives.values());
    }

    public Objective getObjective(DisplaySlot slot) throws IllegalArgumentException {
        Validate.notNull(slot, "Slot cannot be null");
        return displaySlots.get(slot);
    }

    public void clearSlot(DisplaySlot slot) throws IllegalArgumentException {
        Validate.notNull(slot, "Slot cannot be null");
        setDisplaySlot(slot, null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Teams

    public Team registerNewTeam(String name) throws IllegalArgumentException {
        return null;
    }

    public Team getPlayerTeam(OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        return null;
    }

    public Team getTeam(String teamName) throws IllegalArgumentException {
        Validate.notNull(teamName, "Player cannot be null");
        return null;
    }

    public Set<Team> getTeams() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Scores

    public Set<String> getEntries() {
        return ImmutableSet.copyOf(scoreMap.keySet());
    }

    public Set<Score> getScores(String entry) throws IllegalArgumentException {
        Validate.notNull(entry, "Entry cannot be null");

        Set<GlowScore> scoreSet = scoreMap.get(entry);
        if (scoreSet == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.<Score>copyOf(scoreSet);
        }
    }

    public void resetScores(String entry) throws IllegalArgumentException {
        Validate.notNull(entry, "Entry cannot be null");

        for (GlowObjective objective : objectives.values()) {
            objective.deleteScore(entry);
        }
        scoreMap.remove(entry);
    }

    ////////////////////////////////////////////////////////////////////////////
    // OfflinePlayer methods

    @Deprecated
    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> result = new HashSet<>();
        for (String name : getEntries()) {
            result.add(Bukkit.getOfflinePlayer(name));
        }
        return Collections.unmodifiableSet(result);
    }

    @Deprecated
    public Set<Score> getScores(OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        return getScores(player.getName());
    }

    @Deprecated
    public void resetScores(OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        resetScores(player.getName());
    }
}
