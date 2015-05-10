package net.glowstone.io.nbt;

import net.glowstone.GlowServer;
import net.glowstone.io.ScoreboardIoService;
import net.glowstone.scoreboard.*;

import java.io.*;

/**
 * An implementation of the {@link ScoreboardIoService} which reads and writes scoreboards
 * in NBT form
 */
public final class NbtScoreboardIoService implements ScoreboardIoService {

    /**
     * The root directory of the scoreboard
     */
    private final File dir;
    private final GlowServer server;

    private static final String SCOREBOARD_SAVE_FILE = "scoreboard.dat";
    
    public NbtScoreboardIoService(GlowServer server, File dir) {
        this.server = server;
        this.dir = dir;
    }

    @Override
    public GlowScoreboard readMainScoreboard() throws IOException {
        return NbtScoreboardIoReader.readMainScoreboard(new File(dir, SCOREBOARD_SAVE_FILE));
    }

    @Override
    public void writeMainScoreboard(GlowScoreboard scoreboard) throws IOException {
        NbtScoreboardIoWriter.writeMainScoreboard(new File(dir, SCOREBOARD_SAVE_FILE), scoreboard);
    }

    @Override
    public void unload() throws IOException {
        save();
    }

    @Override
    public void save() throws IOException {
        writeMainScoreboard(server.getScoreboardManager().getMainScoreboard());
    }
}
