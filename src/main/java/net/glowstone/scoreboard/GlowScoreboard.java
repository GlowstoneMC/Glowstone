package net.glowstone.scoreboard;

import net.glowstone.GlowServer;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;

import java.util.Set;

/**
 * Scoreboard implementation.
 */
public class GlowScoreboard implements Scoreboard {

    private final GlowServer server;

    public GlowScoreboard(GlowServer server) {
        this.server = server;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Objectives

    public Objective registerNewObjective(String name, String criteria) throws IllegalArgumentException {
        return null;
    }

    public Objective getObjective(String name) throws IllegalArgumentException {
        return null;
    }

    public Set<Objective> getObjectivesByCriteria(String criteria) throws IllegalArgumentException {
        return null;
    }

    public Set<Objective> getObjectives() {
        return null;
    }

    public Objective getObjective(DisplaySlot slot) throws IllegalArgumentException {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Objectives

    public Set<Score> getScores(OfflinePlayer player) throws IllegalArgumentException {
        return null;
    }

    public void resetScores(OfflinePlayer player) throws IllegalArgumentException {

    }

    public Team getPlayerTeam(OfflinePlayer player) throws IllegalArgumentException {
        return null;
    }

    public Team getTeam(String teamName) throws IllegalArgumentException {
        return null;
    }

    public Set<Team> getTeams() {
        return null;
    }

    public Team registerNewTeam(String name) throws IllegalArgumentException {
        return null;
    }

    public Set<OfflinePlayer> getPlayers() {
        return null;
    }

    public void clearSlot(DisplaySlot slot) throws IllegalArgumentException {

    }
}
