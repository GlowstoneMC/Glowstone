package net.glowstone.entity.projectile;

import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.monster.GlowBlaze;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GlowSnowball extends GlowProjectile implements Snowball {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.03;

    /**
     * Creates a thrown snowball with default speed.
     *
     * @param location the position and facing of the thrower
     */
    public GlowSnowball(Location location) {
        super(location);
        setAirDrag(0.99);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        setApplyDragBeforeAccel(true);
        setVelocity(location.getDirection().multiply(3));
        setBoundingBox(0.25, 0.25);
    }

    /**
     * Process collide with a block.
     *
     * @param block the block that the snowball collides with
     */
    @Override
    public void collide(Block block) {
        getWorld().spawnParticle(Particle.SNOWBALL, location, 5);
        remove();
    }

    /**
     * Process collide with a living entity.
     *
     * @param entity the eneity that the snowball collides with
     */
    @Override
    public void collide(LivingEntity entity) {
        ProjectileSource source = getShooter();
        GlowLivingEntity impl = (GlowLivingEntity) entity;
        // the entity receives fake damage.
        if (source instanceof Entity) {
            if (entity instanceof GlowBlaze) {
                impl.damage(3, (Entity) source, EntityDamageEvent.DamageCause.PROJECTILE);
            } else {
                impl.damage(0, (Entity) source, EntityDamageEvent.DamageCause.PROJECTILE);
            }
        } else {
            impl.damage(0, EntityDamageEvent.DamageCause.PROJECTILE);
        }
        collide(location.getBlock());
    }

    @Override
    protected int getObjectId() {
        return EntityNetworkUtil.getObjectId(EntityType.SNOWBALL);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Material.SNOWBALL);
    }

    @Override
    public void setItem(@NotNull ItemStack itemStack) {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
