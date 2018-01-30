package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

public class GlowUndeadHorse extends GlowAbstractHorse implements AbstractHorse {

    public GlowUndeadHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public boolean isUndead() {
        return true;
    }
}
