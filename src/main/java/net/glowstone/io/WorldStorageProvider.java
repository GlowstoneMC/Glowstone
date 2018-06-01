package net.glowstone.io;

import java.io.File;
import net.glowstone.GlowWorld;

/**
 * Interface for providers of world data storage, including chunks and various metadata.
 */
public interface WorldStorageProvider {

    /**
     * Initializes the storage to correspond to the given world.
     *
     * @param world The world to use.
     */
    void setWorld(GlowWorld world);

    /**
     * Gets the folder holding the world data, if the filesystem is being used.
     *
     * @return The world folder, or null.
     */
    File getFolder();

    /**
     * Gets the {@link ChunkIoService} for this world, to be used for reading and writing chunk
     * data.
     *
     * @return The {@link ChunkIoService}.
     */
    ChunkIoService getChunkIoService();

    /**
     * Gets the {@link WorldMetadataService} for this world, to be used for reading and writing
     * world metadata (seed, time, so on).
     *
     * @return The {@link WorldMetadataService}.
     */
    WorldMetadataService getMetadataService();

    /**
     * Gets the {@link PlayerDataService} for this world, to be used for reading and writing data
     * for online and offline players.
     *
     * @return The {@link PlayerDataService}.
     */
    PlayerDataService getPlayerDataService();

    /**
     * Gets the {@link StructureDataService} for this world, to be used for reading and writing data
     * for structures.
     *
     * @return The {@link StructureDataService}.
     */
    StructureDataService getStructureDataService();

    /**
     * Gets the {@link ScoreboardIoService} for this world, to be used for reading and writing data
     * for scoreboards.
     *
     * @return The {@link ScoreboardIoService}.
     */
    ScoreboardIoService getScoreboardIoService();

    /**
     * Gets the {@link PlayerStatisticIoService} for this world, to be used for reading and writing
     * player statistics.
     *
     * @return The {@link PlayerStatisticIoService}.
     */
    PlayerStatisticIoService getPlayerStatisticIoService();

    /**
     * Gets the {@link FunctionIoService} for this world, to be used for reading and writing
     * functions.
     *
     * @return The {@link FunctionIoService}.
     */
    FunctionIoService getFunctionIoService();
}
