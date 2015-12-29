package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSkeleton;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.Skeleton;

public class SkeletonStore extends MonsterStore<GlowSkeleton> {

    public SkeletonStore() {
        super(GlowSkeleton.class, "Skeleton");
    }

    @Override
    public void load(GlowSkeleton entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setSkeletonType(Skeleton.SkeletonType.getType(tag.getInt("SkeletonType")));
    }

    @Override
    public void save(GlowSkeleton entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("SkeletonType", entity.getSkeletonType().getId());
    }

}
