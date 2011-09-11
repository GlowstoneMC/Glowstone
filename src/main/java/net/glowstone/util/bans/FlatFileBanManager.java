package net.glowstone.util.bans;

import net.glowstone.GlowServer;
import net.glowstone.util.PlayerListFile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of BanManager that uses PlayerListFiles.
 */
public class FlatFileBanManager implements BanManager {

    private final PlayerListFile bannedNames;
    private final PlayerListFile bannedIps;

    public FlatFileBanManager(GlowServer server) {
        this.bannedIps = new PlayerListFile(new File(server.getConfigDir(), "banned-ips.txt"));
        this.bannedNames = new PlayerListFile(new File(server.getConfigDir(), "banned-names.txt"));
    }

    public void load() {
        bannedIps.load();
        bannedNames.load();
    }

    public boolean isBanned(String player) {
        return bannedNames.contains(player);
    }

    public boolean setBanned(String player, boolean banned) {
        boolean alreadyBanned = !(isBanned(player) == banned);
        if (banned) {
            bannedNames.add(player);
        } else {
            bannedNames.remove(player);
        }
        return alreadyBanned;
    }

    public Set<String> getBans() {
        return new HashSet<String>(bannedNames.getContents());
    }

    public String getBanMessage(String name) {
        return "You are banned from this server";
    }

    public boolean isIpBanned(String address) {
        return bannedIps.contains(address);
    }

    public boolean setIpBanned(String address, boolean banned) {
        boolean alreadyBanned = !(isIpBanned(address) == banned);
        if (banned) {
            bannedIps.add(address);
        } else {
            bannedIps.remove(address);
        }
        return alreadyBanned;
    }

    public Set<String> getIpBans() {
        return new HashSet<String>(bannedIps.getContents());
    }

    public String getIpBanMessage(String address) {
        return "You are banned from this server";
    }

    public boolean isBanned(String player, String address) {
        return isBanned(player) || isIpBanned(address);
    }
    
}
