package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

public class GlowMooshroom extends GlowAnimal implements MushroomCow {

    public GlowMooshroom(Location location) {
        super(location, EntityType.MUSHROOM_COW, 10);
        setSize(0.9F, 1.3F);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_COW_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_COW_DEATH;
    }
}
