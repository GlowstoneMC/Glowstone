package net.glowstone.io.anvil;

import net.glowstone.GlowWorld;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.PlayerDataService;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.nbt.NbtPlayerDataService;
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
    private PlayerDataService players;

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
    public PlayerDataService getPlayerDataService() {
        if (players == null) {
            players = new NbtPlayerDataService(world.getServer(), new File(dir, "playerdata"));
        }
        return players;
    }
}
