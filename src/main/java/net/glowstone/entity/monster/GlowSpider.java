package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;

public class GlowSpider extends GlowMonster implements Spider {

    public GlowSpider(Location loc) {
        super(loc, EntityType.SPIDER, 16);
        setBoundingBox(1.4, 0.9);
    }

    public boolean isClimbing() {
        return metadata.getByte(MetadataIndex.SPIDER_CLIMBING) == 1;
    }

    public void setClimbing(boolean climbing) {
        metadata.set(MetadataIndex.SPIDER_CLIMBING, climbing ? (byte) 1 : (byte) 0);
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
