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
    public Sound getDeathSound() {
        return Sound.ENTITY_STRAY_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_STRAY_HURT;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_STRAY_AMBIENT;
    }
}
