package net.glowstone.scoreboard;

import net.glowstone.GlowServer;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * ScoreboardManager implementation.
 */
public final class GlowScoreboardManager implements ScoreboardManager {

    private final GlowScoreboard mainScoreboard;
    private final GlowServer server;

    public GlowScoreboardManager(GlowServer server) {
        this.server = server;
        mainScoreboard = new GlowScoreboard(server);
    }

    public GlowScoreboard getMainScoreboard() {
        return mainScoreboard;
    }

    public GlowScoreboard getNewScoreboard() {
        return new GlowScoreboard(server);
    }
}
