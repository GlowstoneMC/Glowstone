package net.glowstone.io.nbt;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.io.ScoreboardIoService;
import net.glowstone.scoreboard.*;
import net.glowstone.util.nbt.CompoundTag;;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


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

    private final String SCOREBOARD_SAVE_FILE = "scoreboard.dat";
    
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
    public void save() throws IOException{
        writeMainScoreboard(server.getScoreboardManager().getMainScoreboard());
    }
}
