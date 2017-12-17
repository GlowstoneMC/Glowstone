package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SkeletonHorse;

public class GlowSkeletonHorse extends GlowUndeadHorse implements SkeletonHorse {

    public GlowSkeletonHorse(Location location) {
        super(location, EntityType.SKELETON_HORSE, 15);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SKELETON_HORSE_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SKELETON_HORSE_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SKELETON_HORSE_AMBIENT;
    }
}
