package net.glowstone.entity.passive;

import net.glowstone.inventory.GlowHorseInventory;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;

public class GlowUndeadHorse extends GlowAbstractHorse implements AbstractHorse {

    public GlowUndeadHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public GlowHorseInventory getInventory() {
        return null;
    }

    @Override
    public boolean isUndead() {
        return true;
    }
}
