package net.glowstone.io.entity;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;

class AnimalStore<T extends GlowAnimal> extends EntityStore<T> {

    private final Constructor<T> constructor;

    public AnimalStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
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
}
