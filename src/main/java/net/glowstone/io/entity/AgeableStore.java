package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.GlowAgeable;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class AgeableStore<T extends GlowAgeable> extends CreatureStore<T> {

    private Function<Location, ? extends T> creator;

    public AgeableStore(Class<T> clazz, EntityType type, Function<Location, ? extends T> creator) {
        super(clazz, type);
        this.creator = creator;
    }

    public AgeableStore(Class<T> clazz, String type, Function<Location, ? extends T> creator) {
        super(clazz, type);
        this.creator = creator;
    }

    @Override
    public T createEntity(Location location, CompoundTag compound) {
        try {
            return creator.apply(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.readInt("Age", entity::setAge);
        compound.readBoolean("AgeLocked", entity::setAgeLock);
        compound.readInt("InLove", entity::setInLove);
        compound.readInt("ForcedAge", entity::setForcedAge);
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
