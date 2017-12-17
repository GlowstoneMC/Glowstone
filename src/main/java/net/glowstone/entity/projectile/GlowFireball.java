package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

// TODO: stubs
public class GlowFireball extends GlowProjectile implements Fireball {
    public GlowFireball(Location location) {
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

    @Override
    public void setDirection(Vector vector) {

    }

    @Override
    public Vector getDirection() {
        return null;
    }

    @Override
    public void setYield(float v) {

    }

    @Override
    public float getYield() {
        return 0;
    }

    @Override
    public void setIsIncendiary(boolean b) {

    }

    @Override
    public boolean isIncendiary() {
        return false;
    }
}
