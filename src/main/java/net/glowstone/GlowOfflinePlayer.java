package net.glowstone;

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
    private String name;
    private UUID uuid;

    private boolean hasPlayed = false;
    private long firstPlayed;
    private long lastPlayed;
    private Location bedSpawn;

    /**
     * Create a new offline player for the given name. If possible, the
     * player's UUID will be found and then their data.
     * @param server The server of the offline player. Must not be null.
     * @param name The name of the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, String name) {
        Validate.notNull(server, "server must not be null");
        Validate.notNull(name, "name must not be null");
        this.server = server;
        this.name = name;
        this.uuid = server.getPlayerDataService().lookupUUID(name);
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
        Validate.notNull(uuid, "uuid must not be null");
        this.server = server;
        this.uuid = uuid;
        loadData();
    }

    private void loadData() {
        try (PlayerDataService.PlayerReader reader = server.getPlayerDataService().beginReadingData(uuid)) {
            hasPlayed = reader.hasPlayedBefore();
            if (hasPlayed) {
                firstPlayed = reader.getFirstPlayed();
                lastPlayed = reader.getLastPlayed();
                bedSpawn = reader.getBedSpawnLocation();

                String lastName = reader.getLastKnownName();
                if (lastName != null) {
                    name = lastName;
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public Player getPlayer() {
        if (uuid != null) {
            return server.getPlayer(uuid);
        } else {
            return server.getPlayerExact(name);
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
        return server.getBanList(BanList.Type.NAME).isBanned(name);
    }

    @Override
    @Deprecated
    public void setBanned(boolean banned) {
        server.getBanList(BanList.Type.NAME).addBan(name, null, null, null);
    }

    @Override
    public boolean isWhitelisted() {
        return server.getWhitelist().containsUUID(uuid);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(this);
        } else {
            server.getWhitelist().remove(uuid);
        }
    }

    @Override
    public boolean isOp() {
        return server.getOpsList().containsUUID(uuid);
    }

    @Override
    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(this);
        } else {
            server.getOpsList().remove(uuid);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Serialization

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("UUID", uuid.toString());
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

        return !(uuid != null ? !uuid.equals(that.uuid) : that.uuid != null);
    }

    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GlowOfflinePlayer{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
