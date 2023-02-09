package net.glowstone.entity.monster;

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
    public Sound getDeathSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_HURT;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_ELDER_GUARDIAN_AMBIENT;
    }

    @Override
    public boolean isElder() {
        return true;
    }
}
