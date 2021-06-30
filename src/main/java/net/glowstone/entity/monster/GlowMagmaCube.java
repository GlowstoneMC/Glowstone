package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;

public class GlowMagmaCube extends GlowSlime implements MagmaCube {

    public GlowMagmaCube(Location loc) {
        super(loc, EntityType.MAGMA_CUBE);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_MAGMA_CUBE_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_MAGMA_CUBE_HURT;
    }
}
