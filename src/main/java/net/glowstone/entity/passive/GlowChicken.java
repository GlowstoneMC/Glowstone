package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;

public class GlowChicken extends GlowAnimal implements Chicken {

    public GlowChicken(Location location) {
        super(location, EntityType.CHICKEN);
        setSize(0.4F, 0.7F);
    }
}
