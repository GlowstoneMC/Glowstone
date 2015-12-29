package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class GlowSlime extends GlowMonster implements Slime {

    private int size;
    private boolean onGround;

    public GlowSlime(Location loc) {
        this(loc, EntityType.SLIME);
    }

    protected GlowSlime(Location loc, EntityType type) {
        super(loc, type);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int sz) {
        this.size = sz;
    }
}
