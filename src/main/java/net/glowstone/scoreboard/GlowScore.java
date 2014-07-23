package net.glowstone.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Implementation/data holder for Scores.
 */
public final class GlowScore implements Score {

    private final GlowObjective objective;
    private final String entry;
    private int score;

    public GlowScore(GlowObjective objective, String entry) {
        this.objective = objective;
        this.entry = entry;
    }

    public Objective getObjective() {
        return objective;
    }

    public Scoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    public String getEntry() {
        return entry;
    }

    @Deprecated
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(entry);
    }

    public int getScore() throws IllegalStateException {
        objective.checkValid();
        return score;
    }

    public void setScore(int score) throws IllegalStateException {
        objective.checkValid();
        this.score = score;
    }
}
