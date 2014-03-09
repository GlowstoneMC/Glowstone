package net.glowstone.scoreboard;

import com.google.common.collect.ImmutableSet;
import net.glowstone.GlowServer;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Scoreboard implementation.
 */
public final class GlowScoreboard implements Scoreboard {

    private final GlowServer server;

    // Objectives
    private final EnumMap<DisplaySlot, GlowObjective> displaySlots = new EnumMap<>(DisplaySlot.class);
    private final HashMap<String, GlowObjective> objectives = new HashMap<>();
    private final HashMap<String, Set<GlowObjective>> criteriaMap = new HashMap<>();

    // Scores

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
    // Players/scores

    public Set<Score> getScores(OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
        return null;
    }

    public void resetScores(OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "Player cannot be null");
    }

    public Set<OfflinePlayer> getPlayers() {
        return null;
    }
}
