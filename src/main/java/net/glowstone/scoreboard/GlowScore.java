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

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public Scoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    @Override
    public String getEntry() {
        return entry;
    }

    @Override
    @Deprecated
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(entry);
    }

    @Override
    public int getScore() throws IllegalStateException {
        objective.checkValid();
        return score;
    }

    /**
     * Sets this score's value.
     * @param score the new value
     * @throws IllegalStateException if the objective is not registered on a scoreboard
     */
    @Override
    public void setScore(int score) throws IllegalStateException {
        objective.checkValid();
        this.score = score;
        objective.getScoreboard()
            .broadcast(new ScoreboardScoreMessage(entry, objective.getName(), score));
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
