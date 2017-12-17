package net.glowstone.entity.projectile;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GlowArrow extends GlowProjectile implements Arrow {

    private volatile PickupStatus customPickupStatus = null;

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
        }
    }

    @Override
    public void collide(Block block) {
    }

    @Override
    public void setOnGround(boolean onGround) {
        super.setOnGround(onGround);
        if (onGround) {
            setVelocity(new Vector(0, 0, 0));
        }
    }

    @Override
    public void collide(LivingEntity entity) {
        entity.damage(6);
        remove();
    }

    @Override
    protected int getObjectId() {
        return 60;
    }

    @Override
    public int getKnockbackStrength() {
        return 0;
    }

    @Override
    public void setKnockbackStrength(int strength) {

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
                getShooter() instanceof Player ? PickupStatus.ALLOWED :
                PickupStatus.DISALLOWED;
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
        return null;
    }
}
