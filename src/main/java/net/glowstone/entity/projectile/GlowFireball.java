package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class GlowFireball extends GlowProjectile implements Fireball {
    private boolean incendiary;
    private float yield;

    public GlowFireball(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        explode();
    }

    @Override
    public void collide(LivingEntity entity) {
        explode();
    }

    private void explode() {
        world.createExplosion(location, yield, incendiary);
        remove();
    }

    @Override
    protected int getObjectId() {
        return (getShooter() instanceof Ghast) ? 63 : 64;
    }

    @Override
    public void setDirection(Vector vector) {
        // TODO
    }

    @Override
    public Vector getDirection() {
        // TODO
        return null;
    }

    @Override
    public void setYield(float v) {
        yield = v;
    }

    @Override
    public float getYield() {
        return yield;
    }

    @Override
    public void setIsIncendiary(boolean b) {
        incendiary = b;
    }

    @Override
    public boolean isIncendiary() {
        return incendiary;
    }
}
