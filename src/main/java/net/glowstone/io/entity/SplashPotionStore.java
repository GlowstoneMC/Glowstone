package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.projectile.GlowSplashPotion;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

public class SplashPotionStore<T extends GlowSplashPotion> extends ProjectileStore<T> {
    public SplashPotionStore(Class<T> clazz, String id, Function<Location, T> constructor) {
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
        tag.consumeItem(entity::setItem, "Potion");
    }
}
