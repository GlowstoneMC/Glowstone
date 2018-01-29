package net.glowstone.io.entity;

import net.glowstone.entity.projectile.GlowSplashPotion;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;

public class SplashPotionStore<T extends GlowSplashPotion> extends ProjectileStore<T> {
    public SplashPotionStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putCompound("Potion", NbtSerialization.writeItem(entity.getItem(), -1));
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isCompound("Potion")) {
            entity.setItem(NbtSerialization.readItem(tag.getCompound("Potion")));
        }
    }
}
