package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;

public class GlowGiant extends GlowMonster implements Giant {

    public GlowGiant(Location loc) {
        super(loc, EntityType.GIANT, 100);
        setSize(3.6F, 10.8F);
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_PLAYER_HURT;
    }
}
