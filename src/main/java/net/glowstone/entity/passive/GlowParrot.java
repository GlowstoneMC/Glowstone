package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class GlowParrot extends GlowAnimal /*implements Parrot*/ {
    public GlowParrot(Location location) {
        super(location, EntityType.UNKNOWN, 6);
    }
}
