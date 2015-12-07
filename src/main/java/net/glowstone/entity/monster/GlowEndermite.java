package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;

public class GlowEndermite extends GlowMonster implements Endermite {

    public GlowEndermite(Location loc) {
        super(loc, EntityType.ENDERMITE);
    }
}
