package net.glowstone.scoreboard;

import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
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
    private boolean locked;

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
        objective.getScoreboard().broadcast(new ScoreboardScoreMessage(entry, objective.getName(), score));
    }

    @Override
    public boolean isScoreSet() throws IllegalStateException {
        objective.checkValid();
        return objective.getScoreboard().getScores(entry).contains(this);
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
