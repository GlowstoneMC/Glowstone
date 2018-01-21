package net.glowstone.entity.monster;

import net.glowstone.entity.annotation.EntityProperties;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

@EntityProperties(arthropod = true)
public class GlowCaveSpider extends GlowMonster implements CaveSpider {

    public GlowCaveSpider(Location loc) {
        super(loc, EntityType.CAVE_SPIDER, 12);
        setBoundingBox(0.7, 0.5);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SPIDER_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SPIDER_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SPIDER_AMBIENT;
    }
}
