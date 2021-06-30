package net.glowstone.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Explosive;

public abstract class GlowExplosive extends GlowEntity implements Explosive {

    @Getter
    @Setter
    private float yield;
    @Getter
    private boolean incendiary;

    /**
     * Creates a non-incendiary instance.
     *
     * @param location the location
     * @param yield    the explosive strength
     */
    public GlowExplosive(Location location, float yield) {
        super(location);
        this.yield = yield;
        incendiary = false;
    }

    @Override
    public void setIsIncendiary(boolean isIncendiary) {
        incendiary = isIncendiary;
    }
}
