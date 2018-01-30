package net.glowstone.scoreboard;

import java.io.IOException;
import net.glowstone.GlowServer;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * ScoreboardManager implementation.
 */
public final class GlowScoreboardManager implements ScoreboardManager {

    private final GlowServer server;
    private GlowScoreboard mainScoreboard;

    public GlowScoreboardManager(GlowServer server) {
        this.server = server;
    }

    @Override
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

    @Override
    public GlowScoreboard getNewScoreboard() {
        return new GlowScoreboard();
    }
}
