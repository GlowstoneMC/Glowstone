package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class GlowSkeleton extends GlowMonster implements Skeleton {

    public GlowSkeleton(Location loc) {
        this(loc, EntityType.SKELETON, 20);
    }

    public GlowSkeleton(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
        setBoundingBox(0.6, 1.99);
    }

    @Override
    public SkeletonType getSkeletonType() {
        return SkeletonType.NORMAL;
    }

    @Override
    public void setSkeletonType(SkeletonType type) {
        //TODO - 1.11 This field was removed since different types are different entities
        //metadata.set(MetadataIndex.SKELETON_TYPE, conversionTime > 0);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SKELETON_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SKELETON_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    public boolean isUndead() {
        return true;
    }
}
