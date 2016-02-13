package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;

import java.util.Random;

public class GlowMagmaCube extends GlowSlime implements MagmaCube {

    public GlowMagmaCube(Location loc) {
        super(loc, EntityType.MAGMA_CUBE);
        Random r = new Random();
        byte size = 1;
        double health = 1;
        switch (r.nextInt(3)) {
            case 0:
                size = 1;
                health = 1;
                break;
            case 1:
                size = 2;
                health = 4;
                break;
            case 2:
                size = 4;
                health = 16;
                break;
        }
        setSize(size);
        setMaxHealthAndHealth(health);
    }
}
