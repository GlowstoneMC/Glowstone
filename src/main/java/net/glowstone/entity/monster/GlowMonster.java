package net.glowstone.entity.monster;

import net.glowstone.entity.GlowCreature;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

public class GlowMonster extends GlowCreature implements Monster {

    /**
     * The range in blocks outside of which the hostile mob will despawn immediately.
     */
    private static final double DESPAWN_RANGE_IMMEDIATE = 128;

    /**
     * Creates a new non-passive mob.
     *
     * @param loc       The location of the non-passive mob.
     * @param type      The type of mob.
     * @param maxHealth The max health for this mob.
     */
    public GlowMonster(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }

    /**
     * Whether the hostile mob can despawn immediately.
     *
     * @return true if the mob can despawn immediately, false otherwise
     */
    public boolean canDespawnImmediately() {
        if (isRemoved()) {
            return false;
        }
        if (world.getPlayerCount() == 0) {
            return false;
        }
        if (isCustomNameVisible()) {
            return false;
        }
        boolean hasNearbyPlayer = world.getPlayers().stream()
            .anyMatch(player -> location.distanceSquared(player.getLocation())
                <= DESPAWN_RANGE_IMMEDIATE * DESPAWN_RANGE_IMMEDIATE);
        if (hasNearbyPlayer) {
            return false;
        }
        return true;
    }

    @Override
    public void pulse() {
        if (canDespawnImmediately()) {
            remove();
            return;
        }
        super.pulse();
    }
}
