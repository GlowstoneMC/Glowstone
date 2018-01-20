package net.glowstone.scoreboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Implementation/data holder for Scores.
 */
@RequiredArgsConstructor
public final class GlowScore implements Score {

    @Getter
    private final GlowObjective objective;
    @Getter
    private final String entry;
    private int score;
    @Setter
    private boolean locked;

    @Override
    public Scoreboard getScoreboard() {
        return objective.getScoreboard();
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
}
