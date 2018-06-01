package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieHorse;

public class GlowZombieHorse extends GlowUndeadHorse implements ZombieHorse {

    public GlowZombieHorse(Location location) {
        super(location, EntityType.ZOMBIE_HORSE, 15);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }
}
