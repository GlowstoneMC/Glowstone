package net.glowstone.scoreboard;

import net.glowstone.GlowServer;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.IOException;

/**
 * ScoreboardManager implementation.
 */
public final class GlowScoreboardManager implements ScoreboardManager {

    private final GlowServer server;
    private GlowScoreboard mainScoreboard = null;

    public GlowScoreboardManager(GlowServer server) {
        this.server = server;
    }

    public GlowScoreboard getMainScoreboard() {
        if (mainScoreboard == null) {
            GlowScoreboard newScoreboard;
            try {
                newScoreboard = server.getScoreboardIoService().readMainScoreboard();
            } catch (IOException e) {
                newScoreboard = new GlowScoreboard();
            }
            mainScoreboard = newScoreboard;
        }
        return mainScoreboard;

    }

    public GlowScoreboard getNewScoreboard() {
        return new GlowScoreboard();
    }
}
