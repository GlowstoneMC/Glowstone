package net.glowstone.entity;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;

/**
 * Represents an Animal, such as a Cow
 */
public class GlowAnimal extends GlowAgeable implements Animals {

    /**
     * Creates a new ageable animal.
     *
     * @param location The location of the animal.
     * @param type     The type of animal.
     * @param maxHealth The max health of this animal.
     */
    public GlowAnimal(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }
}
