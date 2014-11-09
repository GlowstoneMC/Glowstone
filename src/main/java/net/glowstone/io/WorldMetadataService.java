package net.glowstone.io;

import java.io.IOException;
import java.util.UUID;

/**
 * Provider of I/O for world metadata.
 */
public interface WorldMetadataService {

    /**
     * Reads the world's metadata from storage, including final values such as
     * seed and UUID that are only set on first load.
     * @return A {@link WorldFinalValues} with the seed and UUID.
     * @throws IOException if an I/O error occurs.
     */
    WorldFinalValues readWorldData() throws IOException;

    /**
     * Write the world's metadata to storage.
     * @throws IOException if an I/O error occurs.
     */
    void writeWorldData() throws IOException;

    /**
     * A structure representing properties stored about a world that cannot be
     * changed after its initialization, namely seed and UUID.
     */
    class WorldFinalValues {
        private final long seed;
        private final UUID uuid;

        public WorldFinalValues(long seed, UUID uuid) {
            this.seed = seed;
            this.uuid = uuid;
        }

        public long getSeed() {
            return seed;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
