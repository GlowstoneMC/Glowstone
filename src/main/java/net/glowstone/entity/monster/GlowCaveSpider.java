package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class GlowCaveSpider extends GlowMonster implements CaveSpider {

    public GlowCaveSpider(Location loc) {
        super(loc, EntityType.CAVE_SPIDER, 12);
        setBoundingBox(0.7, 0.5);
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_SPIDER_HURT;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_SPIDER_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    public boolean isArthropod() {
        return true;
    }
}
