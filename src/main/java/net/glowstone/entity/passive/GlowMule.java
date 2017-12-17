package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mule;
import org.bukkit.inventory.Inventory;

public class GlowMule extends GlowChestedHorse implements Mule {

    public GlowMule(Location location) {
        super(location, EntityType.MULE, 15);
    }

    @Override
    public Inventory getInventory() {
        return null; // todo
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_MULE_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_MULE_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_MULE_AMBIENT;
    }
}
