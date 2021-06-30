package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illager;

/**
 * An "illager" is a type of hostile mob that regularly spawns in woodland mansions, patrols, raids, and pillager outposts.
 */
public abstract class GlowIllager extends GlowRaider implements Illager {
    public GlowIllager(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }
}
