package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;

public class GlowMagmaCube extends GlowSlime implements MagmaCube {

    public GlowMagmaCube(Location loc) {
        super(loc, EntityType.MAGMA_CUBE);
    }
}
