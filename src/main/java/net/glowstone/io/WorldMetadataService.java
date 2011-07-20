package net.glowstone.io;

import net.glowstone.entity.GlowPlayer;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;


public interface WorldMetadataService {

    /**
     * Reads the data from a chunk's world
     * @return a map with world information
     * @throws IOException if an I/O error occurs
     */
    public WorldFinalValues readWorldData() throws IOException;

    /**
     * Writes data for a chunk's world
     * @throws IOException in the event of unanticipated error
     */
    public void writeWorldData() throws IOException;

    public class WorldFinalValues {
        private final long seed;
        private final UUID uid;

        public WorldFinalValues(long seed, UUID uid) {
            this.seed = seed;
            this.uid = uid;
        }

        public long getSeed() {
            return seed;
        }

        public UUID getUuid() {
            return uid;
        }
    }

    /**
     * Read  player's data from their storage file
     * @param player The player to fetch data for
     * @return a Map with the player's data
     * @throws IOException in the event of unanticipated error
     */
    public void readPlayerData(GlowPlayer player);

    /**
     * Write a player's data to their storage file
     * @param player The player to save data for
     */
    public void writePlayerData(GlowPlayer player);
}
