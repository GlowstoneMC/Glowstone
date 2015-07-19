package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;

/**
 *
 * @author TheMCPEGamer
 */
public class GlowSpider extends GlowMonster implements Spider
{
    public GlowSpider(Location loc)
    {
        super(loc, EntityType.SPIDER);
    }
}
