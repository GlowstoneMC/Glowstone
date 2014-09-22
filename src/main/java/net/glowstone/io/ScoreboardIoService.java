package net.glowstone.io;

import net.glowstone.scoreboard.GlowScoreboard;

import java.io.IOException;

/**
 * Provider of scoreboard I/O services. Implemented by classes to provide
 * a way of saving and loading scoreboards to external storage
 */
public interface ScoreboardIoService {
    /**
     * Loads the main scoreboard.
     * @throws IOException if an I/O error occurs.
     * @return {@link GlowScoreboard} The {@link GlowScoreboard} read from storage
     */
    public GlowScoreboard readMainScoreboard() throws IOException;

    /**
     * Writes the main scoreboard.
     * @param scoreboard The {@link GlowScoreboard} to write.
     * @throws IOException if an I/O error occurs.
     */
    public void writeMainScoreboard(GlowScoreboard scoreboard) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     * @throws IOException if an I/O error occurs.
     */
    public void unload() throws IOException;

    void save() throws IOException;
}
