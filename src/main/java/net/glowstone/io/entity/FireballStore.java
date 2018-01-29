package net.glowstone.io.entity;

import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.projectile.GlowFireball;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.util.Vector;

public class FireballStore<T extends GlowFireball> extends ProjectileStore<T> {

    private static final String IS_INCENDIARY = "glowstone:IsIncendiary";
    private static final String YIELD_INT = "ExplosionPower";
    private static final String YIELD_FLOAT = "glowstone:ExplosionPowerFloat";

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
        tag.putBool(IS_INCENDIARY, entity.isIncendiary());
        tag.putInt(YIELD_INT, (int) entity.getYield());
        tag.putFloat(YIELD_FLOAT, (int) entity.getYield());
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isByte(IS_INCENDIARY)) {
            entity.setIsIncendiary(tag.getBool(IS_INCENDIARY));
        }
        if (tag.isFloat(YIELD_FLOAT)) {
            entity.setYield(tag.getFloat(YIELD_FLOAT));
        } else if (tag.isInt(YIELD_INT)) {
            entity.setYield(tag.getInt(YIELD_INT));
        }
    }
}
