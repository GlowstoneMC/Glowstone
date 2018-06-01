package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Stray;

public class GlowStray extends GlowSkeleton implements Stray {

    public GlowStray(Location loc) {
        super(loc, EntityType.STRAY, 20);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_STRAY_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_STRAY_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_STRAY_AMBIENT;
    }
}
