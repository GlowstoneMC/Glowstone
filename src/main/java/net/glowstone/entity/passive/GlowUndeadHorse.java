package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import net.glowstone.inventory.GlowHorseInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class GlowUndeadHorse extends GlowAbstractHorse implements AbstractHorse {

    private static final Set<Material> BREEDING_FOODS =
        Sets.immutableEnumSet(EnumSet.noneOf(Material.class));

    public GlowUndeadHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public GlowHorseInventory getInventory() {
        return null;
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
