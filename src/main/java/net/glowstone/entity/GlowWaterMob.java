package net.glowstone.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WaterMob;

public abstract class GlowWaterMob extends GlowCreature implements WaterMob {

    public GlowWaterMob(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }
}
