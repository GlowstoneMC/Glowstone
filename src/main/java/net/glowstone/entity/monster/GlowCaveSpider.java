package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class GlowCaveSpider extends GlowMonster implements CaveSpider {
    public GlowCaveSpider(Location loc) {
        super(loc, EntityType.CAVE_SPIDER, 12);
    }
}
