package net.glowstone.io.entity;

import java.lang.reflect.Constructor;
import net.glowstone.entity.GlowAgeable;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class AgeableStore<T extends GlowAgeable> extends CreatureStore<T> {

    private Constructor<T> constructor;

    public AgeableStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
        init(clazz);
    }

    public AgeableStore(Class<T> clazz, String type) {
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

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.containsKey("Age")) {
            entity.setAge(compound.getInt("Age"));
        }
        if (compound.containsKey("AgeLocked")) {
            entity.setAgeLock(compound.getBool("AgeLocked"));
        }
        if (compound.containsKey("InLove")) {
            entity.setInLove(compound.getInt("InLove"));
        }
        if (compound.containsKey("ForcedAge")) {
            entity.setForcedAge(compound.getInt("ForcedAge"));
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Age", entity.getAge());
        tag.putBool("AgeLocked", entity.getAgeLock());
        tag.putInt("InLove", entity.getInLove());
        tag.putInt("ForcedAge", entity.getForcedAge());
    }
}
