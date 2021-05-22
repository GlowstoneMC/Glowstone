package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.GlowWaterMob;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class WaterMobStore<T extends GlowWaterMob> extends EntityStore<T> {

    private final Function<Location, T> creator;

    /**
     * Creates the instance for a mob type.
     *
     * @param clazz   the mob type as a class
     * @param type    the mob type as an {@link EntityType}
     * @param creator the mob type's constructor taking a Location
     */
    public WaterMobStore(Class<T> clazz, EntityType type, Function<Location, T> creator) {
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
}
