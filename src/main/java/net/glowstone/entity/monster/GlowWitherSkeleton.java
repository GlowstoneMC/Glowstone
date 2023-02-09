package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;

public class GlowWitherSkeleton extends GlowSkeleton implements WitherSkeleton {

    public GlowWitherSkeleton(Location loc) {
        super(loc, EntityType.WITHER_SKELETON, 20);
        setBoundingBox(0.7, 2.4);
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_WITHER_SKELETON_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_WITHER_SKELETON_HURT;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_WITHER_SKELETON_AMBIENT;
    }
}
