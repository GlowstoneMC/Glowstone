package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * A monster with a boss bar.
 */
public class GlowBoss extends GlowMonster {
    protected BossBar bar;

    /**
     * Creates a new boss.
     * @param loc The location of the non-passive mob.
     * @param type The type of mob.
     * @param maxHealth The max health for this mob.
     * @param title The boss bar title.
     * @param color The boss bar color.
     * @param style The boss bar style.
     * @param barFlags Flags controlling the boss bar.
     */
    public GlowBoss(Location loc, EntityType type, double maxHealth, String title,
            BarColor color, BarStyle style,
            BarFlag... barFlags) {
        super(loc, type, maxHealth);
        bar = getServer().createBossBar(title, color, style, barFlags);
        bar.setProgress(1);
        for (Player player : loc.getWorld().getPlayers()) {
            // TODO: Check facing direction
            bar.addPlayer(player);
        }
    }

    /**
     * Creates a new boss, whose boss-bar title is equal to its type name.
     * @param loc The location of the non-passive mob.
     * @param type The type of mob.
     * @param maxHealth The max health for this mob.
     * @param color The boss bar color.
     * @param style The boss bar style.
     * @param barFlags Flags controlling the boss bar.
     */
    public GlowBoss(Location loc, EntityType type, double maxHealth, BarColor color, BarStyle style,
            BarFlag... barFlags) {
        this(loc, type, maxHealth, type.getName(), color, style, barFlags);
    }

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        if (health <= 0) {
            bar.removeAll();
        } else {
            bar.setProgress(getHealth() / getMaxHealth());
        }
    }
}
