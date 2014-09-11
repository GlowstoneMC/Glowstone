package net.glowstone.io;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Provider of I/O for player data.
 */
public interface PlayerDataService {

    /**
     * Begin reading player data for online or offline player loading. Some
     * attributes may be read before or without constructing a player entity,
     * see {@link PlayerReader} for more information. When finished with the
     * PlayerReader, {@link PlayerReader#close()} should be called.
     * @param uuid The UUID of the player to read data for.
     * @return The {@link PlayerReader} to use.
     */
    public PlayerReader beginReadingData(UUID uuid);

    /**
     * Shorthand method to read data into an existing player entity.
     * @param player The target player.
     */
    public void readData(GlowPlayer player);

    /**
     * Save all data for an online player.
     * @param player The source player.
     */
    public void writeData(GlowPlayer player);

    /**
     * Get a collection of all known offline players. Currently online
     * players may or may not be included, but if they are, they will be
     * included in OfflinePlayer form.
     * @return All known offline players.
     */
    public Collection<OfflinePlayer> getOfflinePlayers();

    /**
     * Locally look up the UUID of an offline player based on their name. If
     * no local player with the name was found, an offline-mode UUID is
     * returned.
     * @param name The name to look up.
     * @return The UUID of the player.
     */
    public UUID lookupUUID(String name);

    /**
     * A piecewise reader for initializing new players.
     * See {@link PlayerDataService#beginReadingData}.
     */
    public static interface PlayerReader extends AutoCloseable {

        /**
         * Check whether the player has played before. If the player has
         * not played before, most of the rest of the fields will have their
         * default values. If the player has played before, some fields may
         * still not have meaningful values, depending on the data.
         * @return True if the player has played before.
         */
        public boolean hasPlayedBefore();

        /**
         * Get the last saved location of the player if available.
         * @return The location, or null.
         */
        public Location getLocation();

        /**
         * Get the player's bed spawn location if available.
         * @return The location, or null.
         */
        public Location getBedSpawnLocation();

        /**
         * Get the first-played time if available.
         * @return Time in milliseconds since epoch, or 0.
         */
        public long getFirstPlayed();

        /**
         * Get the last-played time if available.
         * @return Time in milliseconds since epoch, or 0.
         */
        public long getLastPlayed();

        /**
         * Get the player's last known username if available.
         * @return The name, or null.
         */
        public String getLastKnownName();

        /**
         * Finish reading the rest of the player's entity data into the
         * specified player entity.
         * @param player The target player.
         */
        public void readData(GlowPlayer player);

        /**
         * Close any resources involved in reading the player data.
         */
        @Override
        public void close();
    }

}
