package net.glowstone.io;

import net.glowstone.GlowWorld;

import java.io.File;

/**
 * Interface for providers of world data storage, including chunks and various
 * metadata.
 */
public interface WorldStorageProvider {

    /**
     * Initialize the storage to correspond to the given world.
     * @param world The world to use.
     */
    public void setWorld(GlowWorld world);

    /**
     * Get the folder holding the world data, if the filesystem is being used.
     * @return The world folder, or null.
     */
    public File getFolder();

    /**
     * Get the {@link ChunkIoService} for this world, to be used for reading
     * and writing chunk data.
     * @return The {@link ChunkIoService}.
     */
    public ChunkIoService getChunkIoService();

    /**
     * Get the {@link WorldMetadataService} for this world, to be used for
     * reading and writing world metadata (seed, time, so on).
     * @return The {@link WorldMetadataService}.
     */
    public WorldMetadataService getMetadataService();

}
