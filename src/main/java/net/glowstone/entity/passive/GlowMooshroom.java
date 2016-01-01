package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

public class GlowMooshroom extends GlowAnimal implements MushroomCow {

    public GlowMooshroom(Location location) {
        super(location, EntityType.MUSHROOM_COW);
        setSize(0.9F, 1.3F);
    }
}
