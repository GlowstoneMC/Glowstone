package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.entity.SpectralArrow;

public class GlowSpectralArrow extends GlowArrow implements SpectralArrow {

    private int glowingTicks;

    public GlowSpectralArrow(Location location) {
        super(location);
    }

    @Override
    public int getGlowingTicks() {
        return glowingTicks;
    }

    @Override
    public void setGlowingTicks(int i) {
        glowingTicks = i;
    }
}
