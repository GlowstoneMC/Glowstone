package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;

public class GlowCow extends GlowAnimal implements Cow {

    public GlowCow(Location location) {
        super(location, EntityType.COW);
        setSize(0.9F, 1.3F);
        setMaxHealthAndHealth(10);
    }
}
