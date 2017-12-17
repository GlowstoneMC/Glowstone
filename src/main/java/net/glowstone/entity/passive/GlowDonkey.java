package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

public class GlowDonkey extends GlowChestedHorse implements Donkey {

    public GlowDonkey(Location location) {
        super(location, EntityType.DONKEY, 15);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_DONKEY_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_DONKEY_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_DONKEY_AMBIENT;
    }
}
