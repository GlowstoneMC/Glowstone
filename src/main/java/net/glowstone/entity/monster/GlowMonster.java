package net.glowstone.entity.monster;

import net.glowstone.entity.GlowAgeable;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

public class GlowMonster extends GlowAgeable implements Monster {
    /**
     * Creates a new ageable non-passive mob.
     *
     * @param loc  The location of the non-passive mob.
     * @param type The type of mob.
     */
    public GlowMonster(Location loc, EntityType type) {
        super(loc, type);
    }
}
