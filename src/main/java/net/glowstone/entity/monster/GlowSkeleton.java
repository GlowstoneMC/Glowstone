package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

import java.util.List;

public class GlowSkeleton extends GlowMonster implements Skeleton {
    private SkeletonType skeletonType = SkeletonType.NORMAL;

    public GlowSkeleton(Location loc) {
        super(loc, EntityType.SKELETON);
        setMaxHealthAndHealth(20);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.SKELETON_TYPE, skeletonType.getId());
        return super.createSpawnMessage();
    }

    @Override
    public SkeletonType getSkeletonType() {
        return this.skeletonType;
    }

    @Override
    public void setSkeletonType(SkeletonType type) {
        this.skeletonType = type;
        metadata.set(MetadataIndex.SKELETON_TYPE, skeletonType.getId());
    }
}
