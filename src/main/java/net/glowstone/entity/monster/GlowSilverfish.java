package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class GlowSilverfish extends GlowMonster implements Silverfish {
    public GlowSilverfish(Location loc) {
        super(loc, EntityType.SILVERFISH, 8);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SILVERFISH_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SILVERFISH_HURT;
    }
}
