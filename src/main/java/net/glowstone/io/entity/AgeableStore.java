package net.glowstone.io.entity;

import net.glowstone.entity.GlowAgeable;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

import java.lang.reflect.Constructor;

class AgeableStore<T extends GlowAgeable> extends CreatureStore<T> {

    private final Constructor<T> constructor;

    public AgeableStore(Class<T> clazz, String id) {
        super(clazz, id);
        Constructor<T> ctor = null;
        try {
            ctor = clazz.getConstructor(Location.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.constructor = ctor;
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

    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setAge(compound.getInt("Age"));
        if (compound.containsKey("AgeLocked")) {
            entity.setAgeLock(compound.getBool("AgeLocked"));
        }
    }

    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Age", entity.getAge());
        tag.putBool("AgeLocked", entity.getAgeLock());
    }
}
