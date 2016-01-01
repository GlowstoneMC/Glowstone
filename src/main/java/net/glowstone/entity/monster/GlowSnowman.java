package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;

public class GlowSnowman extends GlowMonster implements Snowman {

    public GlowSnowman(Location loc) {
        super(loc, EntityType.SNOWMAN);
    }

}
