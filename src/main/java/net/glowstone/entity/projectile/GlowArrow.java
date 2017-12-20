package net.glowstone.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class GlowArrow extends GlowProjectile implements Arrow {

    private volatile PickupStatus customPickupStatus = null;
    private final Arrow.Spigot spigot = new GlowArrow.Spigot();
    @Getter @Setter private int knockbackStrength;
    /**
     * How long an entity burns after being shot with a burning arrow.
     */
    private static final int TARGET_BURN_TICKS = 100;

    public GlowArrow(Location location) {
        super(location);
        setBoundingBox(0.5, 0.5);
    }

    @Override
    protected void pulsePhysics() {
        super.pulsePhysics();
        if (!isOnGround()) {
            setVelocity(getVelocity().subtract(new Vector(0, 0.05, 0)));
            location.add(getVelocity());
            if (isTouchingMaterial(Material.WATER)
                    || isTouchingMaterial(Material.STATIONARY_WATER)) {
                setFireTicks(0);
            } else if (isTouchingMaterial(Material.LAVA)
                    || isTouchingMaterial(Material.STATIONARY_LAVA)) {
                setFireTicks(Integer.MAX_VALUE);
            }
        }
    }

    @Override
    public void setOnGround(boolean onGround) {
        super.setOnGround(onGround);
        if (onGround) {
            setVelocity(new Vector(0, 0, 0));
        }
    }


    @Override
    public void collide(Block block) {
        setFireTicks(0); // Arrows stop burning when they land and don't ignite blocks
        // Don't despawn
    }

    @Override
    public void collide(LivingEntity entity) {
        double damage = spigot.getDamage();
        ProjectileSource shooter = getShooter();
        if (isCritical()) {
            damage += 1.0;
        }
        entity.damage(damage, shooter instanceof Entity ? (Entity) shooter : null,
                EntityDamageEvent.DamageCause.PROJECTILE);
        // Burning arrow ignites target, but doesn't stack if target is already on fire.
        if (getFireTicks() > 0 && entity.getFireTicks() < TARGET_BURN_TICKS) {
            entity.setFireTicks(TARGET_BURN_TICKS);
        }
        // TODO: knockback
        entity.setArrowsStuck(entity.getArrowsStuck() + 1);
        remove();
    }

    @Override
    protected int getObjectId() {
        return 60;
    }

    @Override
    public boolean isCritical() {
        return metadata.getBit(MetadataIndex.ARROW_CRITICAL, 0x1);
    }

    @Override
    public void setCritical(boolean critical) {
        metadata.setBit(MetadataIndex.ARROW_CRITICAL, 0x1, critical);
    }

    @Override public PickupStatus getPickupStatus() {
        PickupStatus customPickupStatus = this.customPickupStatus;
        return customPickupStatus != null ? customPickupStatus :
                getShooter() instanceof Monster ? PickupStatus.DISALLOWED :
                PickupStatus.ALLOWED;
    }

    @Override public void setPickupStatus(PickupStatus pickupStatus) {
        customPickupStatus = pickupStatus;
    }

    @Override
    public PickupRule getPickupRule() {
        return null;
    }

    @Override
    public void setPickupRule(PickupRule pickupRule) {

    }

    @Override
    public Arrow.Spigot spigot() {
        return spigot;
    }

    private class Spigot extends Arrow.Spigot {
        @Getter @Setter private volatile double damage;
    }
}
