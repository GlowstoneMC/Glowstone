package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;

public class GlowWitherSkeleton extends GlowSkeleton implements WitherSkeleton {
    public GlowWitherSkeleton(Location loc) {
        super(loc, EntityType.WITHER_SKELETON, 20);
    }
}
