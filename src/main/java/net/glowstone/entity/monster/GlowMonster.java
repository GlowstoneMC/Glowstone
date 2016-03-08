package net.glowstone.entity.monster;

import net.glowstone.entity.GlowCreature;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

public class GlowMonster extends GlowCreature implements Monster {
    /**
     * Creates a new non-passive mob.
     *
     * @param loc  The location of the non-passive mob.
     * @param type The type of mob.
     * @param maxHealth The max health for this mob.
     */
    public GlowMonster(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }
}
