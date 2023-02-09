package net.glowstone.entity.monster;

import net.glowstone.entity.GlowEntity;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

public class GlowGuardian extends GlowMonster implements Guardian {

    public GlowGuardian(Location loc) {
        this(loc, EntityType.GUARDIAN, 30);
        setBoundingBox(0.85, 0.85);
    }

    public GlowGuardian(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }

    @Override
    public boolean setLaser(boolean activated) {
        return false;
    }

    @Override
    public boolean hasLaser() {
        return false;
    }

    @Override
    public boolean isElder() {
        return false;
    }

    @Override
    public void setElder(boolean elder) {
        if (elder == isElder()) {
            return;
        }
        Class<? extends GlowEntity> clazz = elder ? GlowElderGuardian.class : GlowGuardian.class;
        GlowEntity copy = world.spawn(location, clazz);
        CompoundTag tag = new CompoundTag();
        // Copy attributes
        EntityStorage.save(this, tag);
        EntityStorage.load(copy, tag);
        remove();
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_GUARDIAN_HURT;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_GUARDIAN_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_GUARDIAN_AMBIENT;
    }
}
