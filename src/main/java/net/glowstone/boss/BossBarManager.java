package net.glowstone.boss;

import java.util.Collection;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public enum BossBarManager {
    ;
    /**
     * Adds a boss bar for all of its associated players.
     * @param bossBar the boss bar to add
     */
    public static void register(GlowBossBar bossBar) {
        for (Player player : bossBar.getPlayers()) {
            if (player instanceof GlowPlayer) {
                ((GlowPlayer) player).addBossBar(bossBar);
            }
        }
    }

    /**
     * Removes a boss bar from all of its associated players.
     * @param bossBar the boss bar to remove
     */
    public static void unregister(GlowBossBar bossBar) {
        for (Player player : bossBar.getPlayers()) {
            if (player instanceof GlowPlayer) {
                ((GlowPlayer) player).removeBossBar(bossBar);
            }
        }
    }

    /**
     * Stop all boss bars from sending updates to the given player.
     * @param player the player to unsubscribe
     */
    public static void clearBossBars(GlowPlayer player) {
        // This implementation is a little complicated for concurrency reasons.
        Collection<BossBar> bars;
        do {
            bars = player.getBossBars();
            for (BossBar bar : bars) {
                bar.removePlayer(player);
                player.removeBossBar(bar);
            }
        } while (!bars.isEmpty());
    }
}
