package net.glowstone.entity.passive;

import net.glowstone.entity.annotation.EntityProperties;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

@EntityProperties(undead = true)
public class GlowUndeadHorse extends GlowAbstractHorse implements AbstractHorse {

    public GlowUndeadHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
