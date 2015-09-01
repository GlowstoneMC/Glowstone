package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

public class GlowBlaze extends GlowMonster implements Blaze {
    public GlowBlaze(Location loc) {
        super(loc, EntityType.BLAZE);
    }
}
