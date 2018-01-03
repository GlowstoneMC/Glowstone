package net.glowstone.io;

import java.io.IOException;
import java.util.UUID;
import lombok.Data;

/**
 * Provider of I/O for world metadata.
 */
public interface WorldMetadataService {

    /**
     * Reads the world's metadata from storage, including final values such as seed and UUID that
     * are only set on first load.
     *
     * @return A {@link WorldFinalValues} with the seed and UUID.
     */
    WorldFinalValues readWorldData();

    /**
     * Write the world's metadata to storage.
     *
     * @throws IOException if an I/O error occurs.
     */
    void writeWorldData() throws IOException;

    /**
     * A structure representing properties stored about a world that cannot be changed after its
     * initialization, namely seed and UUID.
     */
    @Data
    class WorldFinalValues {
        private final long seed;
        private final UUID uuid;
    }
}
