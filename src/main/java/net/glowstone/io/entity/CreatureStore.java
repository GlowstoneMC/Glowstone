package net.glowstone.io.entity;

import net.glowstone.entity.GlowCreature;
import net.glowstone.util.nbt.CompoundTag;

abstract class CreatureStore<T extends GlowCreature> extends LivingEntityStore<T> {

    public CreatureStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
    }
}
