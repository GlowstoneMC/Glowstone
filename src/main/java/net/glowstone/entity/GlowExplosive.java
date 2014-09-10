package net.glowstone.entity;

import org.bukkit.Location;
import org.bukkit.entity.Explosive;

public abstract class GlowExplosive extends GlowEntity implements Explosive {

    private float yield;
    private boolean incendiary;

    public GlowExplosive(Location location, float yield) {
        super(location);
        this.yield = yield;
        this.incendiary = false;
    }

    @Override
    public void setYield(float yield) {
        this.yield = yield;
    }

    @Override
    public float getYield() {
        return yield;
    }

    @Override
    public void setIsIncendiary(boolean incendiary) {
        this.incendiary = incendiary;
    }

    @Override
    public boolean isIncendiary() {
        return this.incendiary;
    }
}
