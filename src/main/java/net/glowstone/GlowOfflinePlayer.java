package net.glowstone;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public class GlowOfflinePlayer implements OfflinePlayer {

    private final GlowServer server;
    private final String name;

    public GlowOfflinePlayer(GlowServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public boolean isOnline() {
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean isBanned() {
        return server.getBanManager().isBanned(name);
    }

    public void setBanned(boolean banned) {
        server.getBanManager().setBanned(name, banned);
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

    public Player getPlayer() {
        return server.getPlayerExact(name);
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

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();

        ret.put("name", name);
        return ret;
    }

    public static OfflinePlayer deserialize(Map<String, Object> val) {
        return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
    }
}
