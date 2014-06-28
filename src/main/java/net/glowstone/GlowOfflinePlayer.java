package net.glowstone;

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
     * Create a new offline player for the given name, UUID, or both. The
     * caller is responsible for filling in the other data after construction.
     * At most one of name and uuid may be null.
     * @param server The server of the offline player. Must not be null.
     * @param name The name of the player.
     * @param uuid The UUID of the player.
     * @throws IllegalArgumentException if server is null or both of name and
     * uuid are null.
     */
    public GlowOfflinePlayer(GlowServer server, String name, UUID uuid) {
        Validate.notNull(server, "server == null");
        Validate.isTrue(name != null || uuid != null, "name == null && uuid == null");
        this.server = server;
        this.name = name;
        this.uuid = uuid;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public Player getPlayer() {
        if (uuid != null) {
            return server.getPlayer(uuid);
        } else {
            return server.getPlayerExact(name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player properties

    public boolean hasPlayedBefore() {
        return hasPlayed;
    }

    public long getFirstPlayed() {
        return firstPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public Location getBedSpawnLocation() {
        return bedSpawn;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Ban, op, whitelist

    public boolean isBanned() {
        return server.getBanList(BanList.Type.NAME).isBanned(name);
    }

    @Deprecated
    public void setBanned(boolean banned) {
        server.getBanList(BanList.Type.NAME).addBan(name, null, null, null);
    }

    public boolean isWhitelisted() {
        return server.hasWhitelist() && server.getWhitelist().contains(name);
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(name);
        } else {
            server.getWhitelist().remove(name);
        }
    }

    public boolean isOp() {
        return server.getOpsList().contains(name);
    }

    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(name);
        } else {
            server.getOpsList().remove(name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Serialization

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
