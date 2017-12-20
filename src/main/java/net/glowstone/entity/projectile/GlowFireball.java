package net.glowstone.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class GlowFireball extends GlowProjectile implements Fireball {
    @Getter private boolean incendiary;
    @Getter @Setter private float yield;

    public GlowFireball(Location location) {
        super(location);
        setGravity(false); // Fireballs fly in a straight line
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
        ProjectileSource source = getShooter();
        world.createExplosion(source instanceof Entity ? (Entity) source : this,
                location.getX(), location.getY(), location.getZ(), yield, incendiary, true);
        remove();
    }

    @Override
    protected int getObjectId() {
        return (getShooter() instanceof Ghast) ? 63 : 64;
    }

    @Override
    public void setDirection(Vector vector) {
        setVelocity(vector.normalize().multiply(velocity.length()));
    }

    @Override
    public Vector getDirection() {
        return velocity.normalize();
    }

    @Override
    public void setIsIncendiary(boolean b) {
        incendiary = b;
    }
}
