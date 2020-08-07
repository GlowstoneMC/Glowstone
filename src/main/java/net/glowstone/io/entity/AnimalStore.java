package net.glowstone.io.entity;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.function.Function;

public class AnimalStore<T extends GlowAnimal> extends EntityStore<T> {

    private final Function<Location, T> creator;

    public AnimalStore(Class<T> clazz, EntityType type, Function<Location, T> creator) {
        super(clazz, type);
        this.creator = creator;
    }

    public AnimalStore(Class<T> clazz, String type, Function<Location, T> creator) {
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
