package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class GlowSkeleton extends GlowMonster implements Skeleton {
    private SkeletonType skeletonType = SkeletonType.NORMAL;

    public GlowSkeleton(Location loc) {
        super(loc, EntityType.SKELETON);
    }

    @Override
    public SkeletonType getSkeletonType() {
        return this.skeletonType;
    }

    @Override
    public void setSkeletonType(SkeletonType type) {
        this.skeletonType = type;
    }
}
