package net.glowstone.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.EntityNetworkUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class GlowFireball extends GlowProjectile implements Fireball {
    @Getter
    private boolean incendiary;
    @Getter
    @Setter
    private float yield = 1;

    /**
     * Creates a fireball.
     *
     * @param location the initial location
     */
    public GlowFireball(Location location) {
        super(location);
        setGravity(false); // Fireballs fly in a straight line
        setFriction(false);
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
        ProjectileSource shooter = getShooter();
        if (shooter instanceof Ghast) {
            return EntityNetworkUtil.getObjectId(EntityType.FIREBALL);
        } else if (shooter instanceof EnderDragon) {
            return EntityNetworkUtil.getObjectId(EntityType.DRAGON_FIREBALL);
        }
        return EntityNetworkUtil.getObjectId(EntityType.SMALL_FIREBALL);
    }

    @Override
    public Vector getDirection() {
        return velocity.normalize();
    }

    @Override
    public void setDirection(Vector vector) {
        setVelocity(vector.normalize().multiply(velocity.length()));
    }

    @Override
    public void setIsIncendiary(boolean b) {
        incendiary = b;
    }
}
