package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class GlowGhast extends GlowMonster implements Ghast {
    public GlowGhast(Location loc) {
        super(loc, EntityType.GHAST);
    }
}
