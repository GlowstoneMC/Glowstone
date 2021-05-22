package net.glowstone.entity.monster;

import lombok.Getter;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * A monster with a boss bar.
 */
public class GlowBoss extends GlowMonster implements Boss {
    @Getter
    protected final BossBar bossBar;

    /**
     * Creates a new boss.
     *
     * @param loc       The location of the non-passive mob.
     * @param type      The type of mob.
     * @param maxHealth The max health for this mob.
     * @param title     The boss bar title. TODO: i18n
     * @param color     The boss bar color.
     * @param style     The boss bar style.
     * @param barFlags  Flags controlling the boss bar.
     */
    public GlowBoss(Location loc, EntityType type, double maxHealth, String title,
                    BarColor color, BarStyle style,
                    BarFlag... barFlags) {
        super(loc, type, maxHealth);
        bossBar = getServer().createBossBar(title, color, style, barFlags);
        bossBar.setProgress(1);
        for (Player player : world.getPlayers()) {
            // TODO: Check facing direction
            bossBar.addPlayer(player);
        }
    }

    /**
     * Creates a new boss, whose boss-bar title is equal to its type name.
     *
     * @param loc       The location of the non-passive mob.
     * @param type      The type of mob.
     * @param maxHealth The max health for this mob.
     * @param color     The boss bar color.
     * @param style     The boss bar style.
     * @param barFlags  Flags controlling the boss bar.
     */
    public GlowBoss(Location loc, EntityType type, double maxHealth, BarColor color, BarStyle style,
                    BarFlag... barFlags) {
        this(loc, type, maxHealth, type.getName(), color, style, barFlags);
    }

    @Override
    public boolean teleport(Location location) {
        World oldWorld = world;
        boolean result = super.teleport(location);
        worldLock.readLock().lock();
        try {
            if (world != oldWorld) {
                bossBar.removeAll();
                for (GlowPlayer player : world.getRawPlayers()) {
                    bossBar.addPlayer(player);
                }
            }
        } finally {
            worldLock.readLock().unlock();
        }
        return result;
    }

    @Override
    public void remove() {
        bossBar.removeAll();
        super.remove();
    }

    /**
     * Adds this boss's bar for the given player.
     *
     * @param player the player who should see this bar
     */
    public void addBarToPlayer(Player player) {
        bossBar.addPlayer(player);
    }
}
