package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class GlowWitch extends GlowMonster implements Witch {
    public GlowWitch(Location loc) {
        super(loc, EntityType.WITCH);
    }
}
