package net.glowstone.entity.passive;

import net.glowstone.entity.GlowWaterMob;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;

public class GlowSquid extends GlowWaterMob implements Squid {

    public GlowSquid(Location location) {
        super(location, EntityType.SQUID, 10);
        setSize(0.8F, 0.8F);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SQUID_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SQUID_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SQUID_AMBIENT;
    }
}
