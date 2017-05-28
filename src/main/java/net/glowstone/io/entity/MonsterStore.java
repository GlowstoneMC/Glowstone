package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowMonster;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;

public class MonsterStore<T extends GlowMonster> extends EntityStore<T> {

    private Constructor<? extends T> constructor;

    public MonsterStore(Class<? extends T> clazz, EntityType type) {
        super(clazz, type.getName());
        init(clazz);
    }

    public MonsterStore(Class<? extends T> clazz, String type) {
        super(clazz, type);
        init(clazz);
    }

    private void init(Class<? extends T> clazz) {
        Constructor<? extends T> ctor = null;
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
