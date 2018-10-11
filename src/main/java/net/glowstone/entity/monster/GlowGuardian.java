package net.glowstone.entity.monster;

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
    public boolean isElder() {
        return false;
    }

    @Override
    public void setElder(boolean elder) {
        if (!elder) {
            return;
        }
        GlowElderGuardian elderCopy = world.spawn(location, GlowElderGuardian.class);
        CompoundTag tag = new CompoundTag();
        // Copy attributes
        EntityStorage.save(this, tag);
        EntityStorage.load(elderCopy, tag);
        remove();
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GUARDIAN_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_GUARDIAN_AMBIENT;
    }
}
