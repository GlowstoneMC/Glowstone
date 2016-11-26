package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Stray;

public class GlowStray extends GlowSkeleton implements Stray {
    public GlowStray(Location loc) {
        super(loc, EntityType.STRAY, 20);
    }
}
