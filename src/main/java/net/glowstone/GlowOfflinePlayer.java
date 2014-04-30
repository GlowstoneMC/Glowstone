package net.glowstone;

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
public class GlowOfflinePlayer implements OfflinePlayer {

    private final GlowServer server;
    private final String name;
    private final UUID uuid;

    public GlowOfflinePlayer(GlowServer server, String name, UUID uuid) {
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

    public long getFirstPlayed() {
        throw new UnsupportedOperationException();
    }

    public long getLastPlayed() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPlayedBefore() {
        throw new UnsupportedOperationException();
    }

    public Location getBedSpawnLocation() {
        throw new UnsupportedOperationException();
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
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("UUID", uuid.toString());
        return ret;
    }

    public static OfflinePlayer deserialize(Map<String, Object> val) {
        if (val.get("name") != null) {
            // use name
            return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
        } else {
            // use UUID - remove cast when possible
            return ((GlowServer) Bukkit.getServer()).getOfflinePlayer(UUID.fromString(val.get("UUID").toString()));
        }
    }

    @Override
    public String toString() {
        return "GlowOfflinePlayer{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
