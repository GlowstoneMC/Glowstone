package net.glowstone.entity.monster;

import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;

public class GlowElderGuardian extends GlowGuardian implements ElderGuardian {

    public GlowElderGuardian(Location loc) {
        super(loc, EntityType.ELDER_GUARDIAN, 80);
        setBoundingBox(1.9975, 1.9975);
    }

    @Override
    public void setElder(boolean elder) {
        if (elder) {
            return;
        }
        GlowGuardian youngerCopy = world.spawn(location, GlowGuardian.class);
        CompoundTag tag = new CompoundTag();
        // Copy attributes
        EntityStorage.save(this, tag);
        EntityStorage.load(youngerCopy, tag);
        remove();
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_AMBIENT;
    }

    @Override
    public boolean isElder() {
        return true;
    }
}
