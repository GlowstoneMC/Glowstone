package net.glowstone.io.nbt;

import net.glowstone.GlowWorld;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.WorldStorageProvider;

import java.io.File;

public class NbtWorldStorageProvider implements WorldStorageProvider {
    private GlowWorld world;
    private final File dir;
    private NbtChunkIoService service;
    private NbtWorldMetadataService meta;

    public NbtWorldStorageProvider(String name) {
        this(new File(name));
    }

    public NbtWorldStorageProvider(File dir) {
        this.dir = dir;

    }

    public void setWorld(GlowWorld world) {
        if (world != null)
            throw new IllegalArgumentException("World is already set");
        this.world = world;
        service = new NbtChunkIoService();
        meta = new NbtWorldMetadataService(world, dir);
    }
    
    @Override
    public ChunkIoService getChunkIoService() {
        return service;
    }

    @Override
    public WorldMetadataService getMetadataService() {
        return meta;
    }

    public File getFolder() {
        return dir;
    }
}
