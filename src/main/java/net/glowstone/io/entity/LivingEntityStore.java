package net.glowstone.io.entity;

import net.glowstone.entity.GlowLivingEntity;

public abstract class LivingEntityStore<T extends GlowLivingEntity> extends EntityStore<T> {
    
    public LivingEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
}
