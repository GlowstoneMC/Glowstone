package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
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
        metadata.set(MetadataIndex.SKELETON_TYPE, skeletonType.ordinal());
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
