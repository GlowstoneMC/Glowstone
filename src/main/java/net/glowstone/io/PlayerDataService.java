package net.glowstone.io;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Provider of I/O for player data.
 */
public interface PlayerDataService {

    // init read player (has played before, location, etc)
    // finish read player (entity)
    public PlayerReader beginReadingData(UUID uuid);

    // read player data during play
    public void readData(GlowPlayer player);

    // write player data during play
    public void writeData(GlowPlayer player);

    // read offlineplayer
    public void readOfflineData(GlowOfflinePlayer player);

    // get offline player list
    public Collection<OfflinePlayer> getOfflinePlayers();

    /**
     * A piecewise reader for initializing new players.
     * See {@link PlayerDataService#beginReadingData}.
     */
    public static interface PlayerReader {

        public boolean hasPlayedBefore();

        public Location getLocation();

        public void readData(GlowPlayer player);

    }

}
