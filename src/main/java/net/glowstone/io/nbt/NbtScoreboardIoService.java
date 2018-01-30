package net.glowstone.io.nbt;

import java.io.File;
import java.io.IOException;
import net.glowstone.GlowServer;
import net.glowstone.io.ScoreboardIoService;
import net.glowstone.scoreboard.GlowScoreboard;
import net.glowstone.scoreboard.NbtScoreboardIoReader;
import net.glowstone.scoreboard.NbtScoreboardIoWriter;

/**
 * An implementation of the {@link ScoreboardIoService} which reads and writes scoreboards in NBT
 * form.
 */
public final class NbtScoreboardIoService implements ScoreboardIoService {

    private static final String SCOREBOARD_SAVE_FILE = "scoreboard.dat";
    /**
     * The root directory of the scoreboard.
     */
    private final File dir;
    private final GlowServer server;

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
