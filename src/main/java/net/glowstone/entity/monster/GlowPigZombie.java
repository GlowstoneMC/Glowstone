package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

public class GlowPigZombie extends GlowZombie implements PigZombie {

    private int anger;
    private boolean angry;

    public GlowPigZombie(Location loc) {
        super(loc, EntityType.PIG_ZOMBIE);
    }

    @Override
    public int getAnger() {
        return anger;
    }

    @Override
    public void setAnger(int level) {
        this.anger = level;
    }

    @Override
    public void setAngry(boolean angry) {
        this.angry = angry;
    }

    @Override
    public boolean isAngry() {
        return angry;
    }
}
