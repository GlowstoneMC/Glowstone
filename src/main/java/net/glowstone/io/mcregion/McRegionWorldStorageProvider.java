package net.glowstone.io.mcregion;

import net.glowstone.GlowWorld;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.nbt.NbtWorldMetadataService;

import java.io.File;

public class McRegionWorldStorageProvider implements WorldStorageProvider {
    private GlowWorld world;
    private final File dir;
    private McRegionChunkIoService service;
    private NbtWorldMetadataService meta;

    public McRegionWorldStorageProvider(String name) {
        this(new File(name));
    }

    public McRegionWorldStorageProvider(File dir) {
        this.dir = dir;
    }

    public void setWorld(GlowWorld world) {
        if (this.world != null)
            throw new IllegalArgumentException("World is already set");
        this.world = world;
        service = new McRegionChunkIoService(dir);
        meta = new NbtWorldMetadataService(world, dir);
    }

    public ChunkIoService getChunkIoService() {
        return service;
    }

    public WorldMetadataService getMetadataService() {
        return meta;
    }
}
