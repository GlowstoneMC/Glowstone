package net.glowstone.io.anvil;

import net.glowstone.GlowWorld;
import net.glowstone.io.*;
import net.glowstone.io.data.WorldFunctionIoService;
import net.glowstone.io.nbt.NbtPlayerDataService;
import net.glowstone.io.nbt.NbtScoreboardIoService;
import net.glowstone.io.nbt.NbtStructureDataService;
import net.glowstone.io.nbt.NbtWorldMetadataService;

import java.io.File;

/**
 * A {@link WorldStorageProvider} for the Anvil map format.
 */
public class AnvilWorldStorageProvider implements WorldStorageProvider {

    private final File dir;
    private final File dataDir;
    private GlowWorld world;
    private AnvilChunkIoService service;
    private NbtWorldMetadataService meta;
    private StructureDataService structures;
    private PlayerDataService players;
    private ScoreboardIoService scoreboard;
    private PlayerStatisticIoService statistics;
    private FunctionIoService functions;

    public AnvilWorldStorageProvider(File dir) {
        this.dir = dir;
        this.dataDir = new File(dir, "data");
        this.dataDir.mkdirs();
    }

    @Override
    public void setWorld(GlowWorld world) {
        if (this.world != null)
            throw new IllegalArgumentException("World is already set");
        this.world = world;
        service = new AnvilChunkIoService(dir);
        meta = new NbtWorldMetadataService(world, dir);
        dataDir.mkdirs();
        structures = new NbtStructureDataService(world, dataDir);
        functions = new WorldFunctionIoService(world, dataDir);
    }

    @Override
    public File getFolder() {
        return dir;
    }

    @Override
    public ChunkIoService getChunkIoService() {
        return service;
    }

    @Override
    public WorldMetadataService getMetadataService() {
        return meta;
    }

    @Override
    public StructureDataService getStructureDataService() {
        return structures;
    }

    @Override
    public PlayerDataService getPlayerDataService() {
        if (players == null) {
            players = new NbtPlayerDataService(world.getServer(), new File(dir, "playerdata"));
        }
        return players;
    }

    @Override
    public ScoreboardIoService getScoreboardIoService() {
        if (scoreboard == null) {
            scoreboard = new NbtScoreboardIoService(world.getServer(), new File(dir, "data"));
        }
        return scoreboard;
    }

    @Override
    public PlayerStatisticIoService getPlayerStatisticIoService() {
        if (statistics == null) {
            statistics = new PlayerStatisticIoService(world.getServer(), new File(dir, "stats"));
        }
        return statistics;
    }

    @Override
    public FunctionIoService getFunctionIoService() {
        if (functions == null) {
            functions = new WorldFunctionIoService(world, dataDir);
        }
        return functions;
    }
}
