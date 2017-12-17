package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.entity.LingeringPotion;

// TODO: implement lingering behavior
public class GlowLingeringPotion extends GlowSplashPotion implements LingeringPotion {
    public GlowLingeringPotion(Location location) {
        super(location);
    }
}
