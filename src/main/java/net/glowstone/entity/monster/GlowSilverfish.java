package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class GlowSilverfish extends GlowMonster implements Silverfish {

    public GlowSilverfish(Location loc) {
        super(loc, EntityType.SILVERFISH, 8);
        setBoundingBox(0.4, 0.3);
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_SILVERFISH_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_SILVERFISH_HURT;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_SILVERFISH_AMBIENT;
    }

    @Override
    public boolean isArthropod() {
        return true;
    }
}
