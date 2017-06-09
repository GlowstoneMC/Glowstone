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
     *
     * @param world The world to use.
     */
    void setWorld(GlowWorld world);

    /**
     * Get the folder holding the world data, if the filesystem is being used.
     *
     * @return The world folder, or null.
     */
    File getFolder();

    /**
     * Get the {@link ChunkIoService} for this world, to be used for reading
     * and writing chunk data.
     *
     * @return The {@link ChunkIoService}.
     */
    ChunkIoService getChunkIoService();

    /**
     * Get the {@link WorldMetadataService} for this world, to be used for
     * reading and writing world metadata (seed, time, so on).
     *
     * @return The {@link WorldMetadataService}.
     */
    WorldMetadataService getMetadataService();

    /**
     * Get the {@link PlayerDataService} for this world, to be used for
     * reading and writing data for online and offline players.
     *
     * @return The {@link PlayerDataService}.
     */
    PlayerDataService getPlayerDataService();

    StructureDataService getStructureDataService();

    ScoreboardIoService getScoreboardIoService();

    PlayerStatisticIoService getPlayerStatisticIoService();

    FunctionIoService getFunctionIoService();
}
