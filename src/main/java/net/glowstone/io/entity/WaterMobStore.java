package net.glowstone.io.entity;

import java.lang.reflect.Constructor;
import net.glowstone.entity.GlowWaterMob;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class WaterMobStore<T extends GlowWaterMob> extends EntityStore<T> {

    private final Constructor<T> constructor;

    /**
     * Creates the instance for a mob type.
     *
     * @param clazz the mob type as a class
     * @param type the mob type as an {@link EntityType}
     */
    public WaterMobStore(Class<T> clazz, EntityType type) {
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
