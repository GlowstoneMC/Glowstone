package net.glowstone.io.entity;

import net.glowstone.entity.projectile.GlowSplashPotion;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.jetbrains.annotations.NonNls;

import java.util.function.Function;

public class SplashPotionStore<T extends GlowSplashPotion> extends ProjectileStore<T> {
    public SplashPotionStore(Class<T> clazz, @NonNls String id, Function<Location, T> constructor) {
        super(clazz, id, constructor);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putCompound("Potion", NbtSerialization.writeItem(entity.getItem(), -1));
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readItem("Potion", entity::setItem);
    }
}
