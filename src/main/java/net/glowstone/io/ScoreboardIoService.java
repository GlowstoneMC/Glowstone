package net.glowstone.io;

import net.glowstone.scoreboard.GlowScoreboard;

import java.io.IOException;

/**
 * Provider of scoreboard I/O services.
 *
 * <p>Implemented by classes to provide a way of saving and loading scoreboards to external storage
 */
public interface ScoreboardIoService {

    /**
     * Loads the main scoreboard.
     *
     * @return {@link GlowScoreboard} The {@link GlowScoreboard} read from storage
     * @throws IOException if an I/O error occurs.
     */
    GlowScoreboard readMainScoreboard() throws IOException;

    /**
     * Writes the main scoreboard.
     *
     * @param scoreboard The {@link GlowScoreboard} to write.
     * @throws IOException if an I/O error occurs.
     */
    void writeMainScoreboard(GlowScoreboard scoreboard) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     *
     * @throws IOException if an I/O error occurs.
     */
    void unload() throws IOException;

    void save() throws IOException;
}
