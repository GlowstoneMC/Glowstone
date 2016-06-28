package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSkeleton;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.Skeleton.SkeletonType;

class SkeletonStore extends MonsterStore<GlowSkeleton> {

    public SkeletonStore() {
        super(GlowSkeleton.class, "Skeleton");
    }

    @Override
    public void load(GlowSkeleton entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isInt("SkeletonType")) {
            entity.setSkeletonType(SkeletonType.values()[tag.getInt("SkeletonType")]);
        } else {
            entity.setSkeletonType(SkeletonType.NORMAL);
        }
    }

    @Override
    public void save(GlowSkeleton entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("SkeletonType", entity.getSkeletonType().ordinal());
    }

}
