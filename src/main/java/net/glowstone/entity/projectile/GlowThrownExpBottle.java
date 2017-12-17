package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownExpBottle;

// TODO: stubs
public class GlowThrownExpBottle extends GlowProjectile implements ThrownExpBottle {
    public GlowThrownExpBottle(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {

    }

    @Override
    public void collide(LivingEntity entity) {

    }

    @Override
    protected int getObjectId() {
        return 0;
    }
}
