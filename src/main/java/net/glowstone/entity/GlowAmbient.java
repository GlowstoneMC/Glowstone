package net.glowstone.entity;

import org.bukkit.Location;
import org.bukkit.entity.Ambient;

public abstract class GlowAmbient extends GlowMob implements Ambient {

    public GlowAmbient(Location location, double maxHealth) {
        super(location, maxHealth);
    }
}
