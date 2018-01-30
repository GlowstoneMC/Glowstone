package net.glowstone.io.entity;

import java.lang.reflect.Constructor;
import net.glowstone.entity.projectile.GlowProjectile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

class ProjectileStore<T extends GlowProjectile> extends EntityStore<T> {

    private final Constructor<T> constructor;

    public ProjectileStore(Class<T> clazz, String id) {
        super(clazz, id);
        Constructor<T> ctor = null;
        try {
            ctor = clazz.getConstructor(Location.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        constructor = ctor;
    }

    @Override
    public T createEntity(Location location, CompoundTag compound) {
        try {
            return constructor.newInstance(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        // Todo: xTile, yTile, zTile, inTile
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        // Todo: xTile, yTile, zTile, inTile
    }
}
