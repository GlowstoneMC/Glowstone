package net.glowstone;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import net.glowstone.io.PlayerDataService.PlayerReader;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public final class GlowOfflinePlayer implements OfflinePlayer {

    private final GlowServer server;
    private PlayerProfile profile;

    private boolean hasPlayed;
    private long firstPlayed;
    private long lastPlayed;
    private String lastName;
    private Location bedSpawn;

    /**
     * Create a new offline player for the given name. If possible, the player's
     * UUID will be found and then their data.
     *
     * @param server  The server of the offline player. Must not be null.
     * @param profile The profile associated with the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, PlayerProfile profile) {
        checkNotNull(server, "server must not be null");
        checkNotNull(profile, "profile must not be null");
        this.server = server;
        this.profile = profile;
        loadData();
    }

    /**
     * Create a new offline player for the given UUID. If possible, the
     * player's data (including name) will be loaded based on the UUID.
     *
     * @param server The server of the offline player. Must not be null.
     * @param name   The name of the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, String name) {
        checkNotNull(server, "server must not be null");
        checkNotNull(name, "name cannot be null");
        this.server = server;
        profile = PlayerProfile.getProfile(name);
        loadData();
    }

    /**
     * Create a new offline player for the given UUID. If possible, the player's
     * data (including name) will be loaded based on the UUID.
     *
     * @param server The server of the offline player. Must not be null.
     * @param uuid   The UUID of the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, UUID uuid) {
        checkNotNull(server, "server must not be null");
        checkNotNull(uuid, "UUID must not be null");
        this.server = server;
        profile = ProfileCache.getProfile(uuid);
        loadData();
    }

    @SuppressWarnings("UnusedDeclaration")
    public static OfflinePlayer deserialize(Map<String, Object> val) {
        if (val.get("name") != null) {
            // use name
            return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
        } else {
            // use UUID
            return Bukkit.getServer().getOfflinePlayer(UUID.fromString(val.get("UUID").toString()));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    private void loadData() {
        try (PlayerReader reader = server.getPlayerDataService().beginReadingData(getUniqueId())) {
            hasPlayed = reader.hasPlayedBefore();
            if (hasPlayed) {
                firstPlayed = reader.getFirstPlayed();
                lastPlayed = reader.getLastPlayed();
                bedSpawn = reader.getBedSpawnLocation();

                String lastName = reader.getLastKnownName();
                if (lastName != null) {
                    this.lastName = lastName;
                }
            }
        }
    }

    @Override
    public String getName() {
        Player player = getPlayer();
        if (player != null) {
            return player.getName();
        }
        if (profile.getName() != null) {
            return profile.getName();
        }
        if (lastName != null) {
            return lastName;
        }
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return profile.getUniqueId();
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player properties

    @Override
    public Player getPlayer() {
        if (getUniqueId() != null) {
            return server.getPlayer(getUniqueId());
        } else {
            return server.getPlayerExact(getName());
        }
    }

    @Override
    public boolean hasPlayedBefore() {
        return hasPlayed;
    }

    @Override
    public long getFirstPlayed() {
        return firstPlayed;
    }

    @Override
    public long getLastPlayed() {
        return lastPlayed;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Ban, op, whitelist

    @Override
    public Location getBedSpawnLocation() {
        return bedSpawn;
    }

    @Override
    public boolean isBanned() {
        return server.getBanList(Type.NAME).isBanned(getName());
    }

    @Override
    public boolean isWhitelisted() {
        return server.getWhitelist().containsProfile(profile);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(this);
        } else {
            server.getWhitelist().remove(profile);
        }
    }

    @Override
    public boolean isOp() {
        return server.getOpsList().containsUUID(getUniqueId());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Serialization

    @Override
    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(this);
        } else {
            server.getOpsList().remove(profile);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("UUID", getUniqueId().toString());
        return ret;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlowOfflinePlayer that = (GlowOfflinePlayer) o;

        return profile.equals(that.profile);
    }

    public int hashCode() {
        return getUniqueId() != null ? getUniqueId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GlowOfflinePlayer{" +
                "name='" + getName() + '\'' +
                ", uuid=" + getUniqueId() +
                '}';
    }
}
