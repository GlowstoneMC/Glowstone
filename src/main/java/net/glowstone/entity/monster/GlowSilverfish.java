package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class GlowSilverfish extends GlowMonster implements Silverfish {
    public GlowSilverfish(Location loc) {
        super(loc, EntityType.SILVERFISH);
    }
}
