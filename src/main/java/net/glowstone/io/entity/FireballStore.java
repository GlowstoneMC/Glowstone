package net.glowstone.io.entity;

import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.projectile.GlowFireball;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.util.Vector;

public class FireballStore<T extends GlowFireball> extends ProjectileStore<T> {
    public FireballStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        Vector vel = entity.getVelocity();
        // Mojang creates tags "direction" and "power", as duplicates of "Motion"
        final List<Double> velocityAsList = Arrays.asList(vel.getX(), vel.getY(), vel.getZ());
        tag.putList("direction", TagType.LIST, velocityAsList);
        tag.putList("power", TagType.LIST, velocityAsList);
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
    }
}
