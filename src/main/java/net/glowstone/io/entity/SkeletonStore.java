package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSkeleton;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.Skeleton;

public class SkeletonStore extends MonsterStore<GlowSkeleton> {
    public SkeletonStore() {
        super(GlowSkeleton.class, "Skeleton");
    }

    @Override
    public GlowSkeleton createEntity(Location location, CompoundTag compound) {
        return new GlowSkeleton(location);
    }

    @Override
    public void load(GlowSkeleton entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setSkeletonType(Skeleton.SkeletonType.values()[compound.getByte("SkeletonType")]);
    }

    @Override
    public void save(GlowSkeleton entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("SkeletonType", entity.getSkeletonType().ordinal());
    }
}
