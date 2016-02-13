package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;

public class GlowSquid extends GlowAnimal implements Squid {

    public GlowSquid(Location location) {
        super(location, EntityType.SQUID);
        setSize(0.95F, 0.95F);
        setMaxHealthAndHealth(10);
    }
}
