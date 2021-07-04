package net.glowstone.io.entity;

import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.jetbrains.annotations.NonNls;

import java.util.function.Function;

public class ArrowStore<T extends GlowArrow> extends ProjectileStore<T> {

    public ArrowStore(Class<T> clazz, @NonNls String id, Function<Location, T> constructor) {
        super(clazz, id, constructor);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putShort("life", entity.getLife());
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readShort("life", entity::setLife);
    }
}
