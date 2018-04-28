package net.glowstone.io.entity;

import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.entity.GlowHangingEntity.HangingFace;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public abstract class HangingStore<T extends GlowHangingEntity> extends EntityStore<T> {

    public HangingStore(Class<? extends T> clazz, EntityType type) {
        super(clazz, type);
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);

        tag.readByte(facing ->
            entity.setFacingDirection(HangingFace.values()[facing].getBlockFace()), "Facing");
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Facing", HangingFace.getByBlockFace(entity.getFacing()).ordinal());
    }
}
