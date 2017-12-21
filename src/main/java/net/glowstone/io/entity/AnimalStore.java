package net.glowstone.io.entity;

import java.lang.reflect.Constructor;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class AnimalStore<T extends GlowAnimal> extends EntityStore<T> {

    private Constructor<T> constructor;

    public AnimalStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
        init(clazz);
    }

    public AnimalStore(Class<T> clazz, String type) {
        super(clazz, type);
        init(clazz);
    }

    private void init(Class<T> clazz) {
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
