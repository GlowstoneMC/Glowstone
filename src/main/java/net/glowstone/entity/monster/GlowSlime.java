package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import java.util.Random;

public class GlowSlime extends GlowMonster implements Slime {

    private boolean onGround;

    public GlowSlime(Location loc) {
        this(loc, EntityType.SLIME);
    }

    protected GlowSlime(Location loc, EntityType type) {
        super(loc, type, 1);
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
        setMaxHealth(health);
        setHealth(health);
    }

    @Override
    public int getSize() {
        return metadata.getInt(MetadataIndex.SLIME_SIZE);
    }

    @Override
    public void setSize(int sz) {
        metadata.set(MetadataIndex.SLIME_SIZE, sz);
    }
}
