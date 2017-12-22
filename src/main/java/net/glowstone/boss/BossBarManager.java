package net.glowstone.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.glowstone.GlowServer;
import org.bukkit.entity.Player;

public class BossBarManager {

    private static BossBarManager instance;

    private final List<GlowBossBar> bossBars = new ArrayList<>();
    private final GlowServer server;

    /**
     * Create an instance for the given server.
     * @param server the server
     */
    public BossBarManager(GlowServer server) {
        if (instance != null) {
            throw new RuntimeException("BossBar Manager has already been initialized.");
        }
        this.server = server;
        instance = this;
    }

    public static BossBarManager getInstance() {
        return instance;
    }

    /**
     * Returns a list of the current boss bars.
     * @return the current boss bars
     */
    public List<GlowBossBar> getBossBars() {
        return new ArrayList<>(bossBars);
    }

    /**
     * Adds a boss bar if it's not already present.
     * @param bossBar the boss bar to add
     */
    public void register(GlowBossBar bossBar) {
        if (bossBars.contains(bossBar)) {
            return;
        }
        bossBars.add(bossBar);
    }

    /**
     * Removes a boss bar.
     * @param bossBar the boss bar to remove
     */
    public void unregister(GlowBossBar bossBar) {
        bossBar.removeAll();
        bossBars.remove(bossBar);
    }

    /**
     * Stop all boss bars from sending updates to the given player.
     * @param player the player to unsubscribe
     */
    public void clearBossBars(Player player) {
        for (GlowBossBar bossBar : bossBars) {
            bossBar.removePlayer(player);
        }
    }

    /**
     * Returns the boss bar with the given UUID.
     * @param uuid the UUID to look up
     * @return the boss bar with the given UUID, or null if none exists
     */
    public GlowBossBar getBossBar(UUID uuid) {
        for (GlowBossBar bossBar : bossBars) {
            if (bossBar.getUniqueId().equals(uuid)) {
                return bossBar;
            }
        }
        return null;
    }
}
