package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class GlowSkeleton extends GlowMonster implements Skeleton {
    private SkeletonType skeletonType = SkeletonType.NORMAL;

    public GlowSkeleton(Location loc) {
        super(loc, EntityType.SKELETON, 20);
        setBoundingBox(0.6, 1.99);
    }

    @Override
    public SkeletonType getSkeletonType() {
        return skeletonType;
    }

    @Override
    public void setSkeletonType(SkeletonType type) {
        skeletonType = type;
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
}
