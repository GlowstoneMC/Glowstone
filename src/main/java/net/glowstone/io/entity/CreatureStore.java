package net.glowstone.io.entity;

import net.glowstone.entity.GlowCreature;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

abstract class CreatureStore<T extends GlowCreature> extends LivingEntityStore<T> {

    public CreatureStore(Class<T> clazz, EntityType type) {
        super(clazz, type.getName());
    }

    public CreatureStore(Class<T> clazz, String type) {
        super(clazz, type);
    }

    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
    }
}
