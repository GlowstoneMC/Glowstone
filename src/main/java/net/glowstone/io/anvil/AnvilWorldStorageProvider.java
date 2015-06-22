package net.glowstone.io.anvil;

import net.glowstone.GlowWorld;
import net.glowstone.io.*;
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
    private GlowWorld world;
    private AnvilChunkIoService service;
    private NbtWorldMetadataService meta;
    private StructureDataService structures;
    private PlayerDataService players;
    private ScoreboardIoService scoreboard;

    public AnvilWorldStorageProvider(File dir) {
        this.dir = dir;
    }

    @Override
    public void setWorld(GlowWorld world) {
        if (this.world != null)
            throw new IllegalArgumentException("World is already set");
        this.world = world;
        service = new AnvilChunkIoService(dir);
        meta = new NbtWorldMetadataService(world, dir);
        structures = new NbtStructureDataService(world, new File(dir, "data"));
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
            this.scoreboard = new NbtScoreboardIoService(world.getServer(), new File(dir, "data"));
        }
        return scoreboard;
    }
}
