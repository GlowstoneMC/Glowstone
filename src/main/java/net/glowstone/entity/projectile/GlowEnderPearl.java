package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;

// TODO: Stubs
public class GlowEnderPearl extends GlowProjectile implements EnderPearl {
    public GlowEnderPearl(Location location) {
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
