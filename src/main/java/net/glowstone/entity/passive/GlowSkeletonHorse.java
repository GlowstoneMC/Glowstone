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

    @Override
    public boolean isTrapped() {
        return false;
    }

    @Override
    public void setTrapped(boolean trapped) {

    }

    // TODO: 1.13
    @Override
    public int getTrapTime() {
        return 0;
    }

    @Override
    public void setTrapTime(int trapTime) {

    }

    @Override
    public boolean isTrap() {
        return false;
    }

    @Override
    public void setTrap(boolean trap) {

    }
}
