package net.glowstone.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkull;

public class GlowWitherSkull extends GlowFireball implements WitherSkull {
    private static final int NETWORK_ID = 66;
    @Getter
    @Setter
    private boolean charged;

    @Override
    protected int getObjectId() {
        return NETWORK_ID;
    }

    public GlowWitherSkull(Location location) {
        super(location);
    }
}
