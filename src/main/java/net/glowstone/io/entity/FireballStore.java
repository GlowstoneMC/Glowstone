package net.glowstone.io.entity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.glowstone.entity.projectile.GlowFireball;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NonNls;

public class FireballStore<T extends GlowFireball> extends ProjectileStore<T> {

    private static final String IS_INCENDIARY = "glowstone:IsIncendiary";
    private static final String YIELD_INT = "ExplosionPower";
    private static final String YIELD_FLOAT = "glowstone:ExplosionPowerFloat";

    public FireballStore(Class<T> clazz, @NonNls String id, Function<Location, T> constructor) {
        super(clazz, id, constructor);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        Vector vel = entity.getVelocity();
        // Mojang creates tags "direction" and "power", as duplicates of "Motion"
        final List<Double> velocityAsList = Arrays.asList(vel.getX(), vel.getY(), vel.getZ());
        tag.putDoubleList("direction", velocityAsList);
        tag.putDoubleList("power", velocityAsList);
        tag.putBool(IS_INCENDIARY, entity.isIncendiary());
        tag.putInt(YIELD_INT, (int) entity.getYield());
        tag.putFloat(YIELD_FLOAT, (int) entity.getYield());
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readBoolean(IS_INCENDIARY, entity::setIsIncendiary);
        if (!tag.readFloat(YIELD_FLOAT, entity::setYield)) {
            tag.readInt(YIELD_INT, entity::setYield);
        }
    }
}
