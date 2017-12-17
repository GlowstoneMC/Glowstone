package net.glowstone.io.entity;

import net.glowstone.entity.GlowAreaEffectCloud;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class AreaEffectCloudStore extends EntityStore<GlowAreaEffectCloud> {

    public AreaEffectCloudStore() {
        super(GlowAreaEffectCloud.class, EntityType.AREA_EFFECT_CLOUD);
    }

    @Override
    public void load(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.load(entity, tag);
        // TODO
    }

    @Override
    public void save(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.save(entity, tag);
        // TODO
    }

    @Override
    public GlowAreaEffectCloud createEntity(Location location, CompoundTag compound) {
        return new GlowAreaEffectCloud(location);
    }
}
