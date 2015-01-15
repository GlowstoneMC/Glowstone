package net.glowstone;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import net.glowstone.io.PlayerDataService;
import org.apache.commons.lang.Validate;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public final class GlowOfflinePlayer implements OfflinePlayer {

    private final GlowServer server;
    private PlayerProfile profile;


    private boolean hasPlayed = false;
    private long firstPlayed;
    private long lastPlayed;
    private String lastName;
    private Location bedSpawn;

    /**
     * Create a new offline player for the given name. If possible, the
     * player's UUID will be found and then their data.
     * @param server The server of the offline player. Must not be null.
     * @param profile The profile associated with the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, PlayerProfile profile) {
        Validate.notNull(server, "server must not be null");
        Validate.notNull(profile, "profile must not be null");
        this.server = server;
        this.profile = profile;
        loadData();
    }

    /**
     * Create a new offline player for the given UUID. If possible, the
     * player's data (including name) will be loaded based on the UUID.
     * @param server The server of the offline player. Must not be null.
     * @param name The name of the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, String name) {
        Validate.notNull(server, "server must not be null");
        Validate.notNull(name, "name cannot be null");
        this.server = server;
        profile = PlayerProfile.getProfile(name);
        loadData();
    }

    /**
     * Create a new offline player for the given UUID. If possible, the
     * player's data (including name) will be loaded based on the UUID.
     * @param server The server of the offline player. Must not be null.
     * @param uuid The UUID of the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, UUID uuid) {
        Validate.notNull(server, "server must not be null");
        Validate.notNull(uuid, "UUID must not be null");
        this.server = server;
        profile = ProfileCache.getProfile(uuid);
        loadData();
    }

    private void loadData() {
        try (PlayerDataService.PlayerReader reader = server.getPlayerDataService().beginReadingData(getUniqueId())) {
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

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

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

    @Override
    public Player getPlayer() {
        if (getUniqueId() != null) {
            return server.getPlayer(getUniqueId());
        } else {
            return server.getPlayerExact(getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player properties

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

    @Override
    public Location getBedSpawnLocation() {
        return bedSpawn;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Ban, op, whitelist

    @Override
    public boolean isBanned() {
        return server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    @Override
    @Deprecated
    public void setBanned(boolean banned) {
        server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
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

    @Override
    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(this);
        } else {
            server.getOpsList().remove(profile);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Serialization

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("UUID", getUniqueId().toString());
        return ret;
    }

    public static OfflinePlayer deserialize(Map<String, Object> val) {
        if (val.get("name") != null) {
            // use name
            return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
        } else {
            // use UUID
            return Bukkit.getServer().getOfflinePlayer(UUID.fromString(val.get("UUID").toString()));
        }
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
