package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.Explosion;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowWither extends GlowBoss implements Wither {

    @Getter
    private int invulnerableTicks;
    @Getter
    private Entity centerTarget;
    @Getter
    private Entity leftTarget;
    @Getter
    private Entity rightTarget;
    @Getter
    @Setter
    private boolean charged;
    @Setter
    private boolean canTravelThroughPortals;

    /**
     * Creates a wither.
     *
     * @param loc the location
     */
    public GlowWither(Location loc) {
        super(loc, EntityType.WITHER, 300, "Wither", BarColor.BLUE, BarStyle.SOLID);
        setInvulnerableTicks(220);
        setCenterTarget(null);
        setLeftTarget(null);
        setRightTarget(null);
        setHealth(getMaxHealth() / 3);
    }

    @Override
    public void damage(double amount, Entity source, @NotNull EntityDamageEvent.DamageCause cause) {
        if (invulnerableTicks > 0) {
            return;
        }
        super.damage(amount, source, cause);
    }

    @Override
    public void setTarget(@NotNull Head head, @Nullable LivingEntity target) {

    }

    @Override
    public @Nullable LivingEntity getTarget(@NotNull Head head) {
        return null;
    }

    public void setInvulnerableTicks(int invulnerableTicks) {
        this.invulnerableTicks = invulnerableTicks;
        this.metadata.set(MetadataIndex.WITHER_INVULN_TIME, invulnerableTicks);
    }

    /**
     * Sets the center target.
     *
     * @param centerTarget the new center target
     */
    public void setCenterTarget(Entity centerTarget) {
        this.centerTarget = centerTarget;
        setTargetMetadata(centerTarget, MetadataIndex.WITHER_TARGET_1);
    }

    private void setTargetMetadata(Entity target, MetadataIndex index) {
        this.metadata.set(index, target == null ? 0 : target.getEntityId());
    }

    /**
     * Sets the left target.
     *
     * @param leftTarget the new left target
     */
    public void setLeftTarget(Entity leftTarget) {
        this.leftTarget = leftTarget;
        setTargetMetadata(leftTarget, MetadataIndex.WITHER_TARGET_2);
    }

    /**
     * Sets the right target.
     *
     * @param rightTarget the new right target
     */
    public void setRightTarget(Entity rightTarget) {
        this.rightTarget = rightTarget;
        setTargetMetadata(rightTarget, MetadataIndex.WITHER_TARGET_3);
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getInvulnerableTicks() > 0) {
            setInvulnerableTicks(getInvulnerableTicks() - 1);
            if (ticksLived % 10 == 0) {
                setHealth(getHealth() + 10);
            }
            if (getInvulnerableTicks() == 1) {
                getWorld().createExplosion(getLocation(), Explosion.POWER_WITHER_CREATION);
                for (Player player : getServer().getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
                }
            }
        } else if (ticksLived % 20 == 0) {
            setHealth(getHealth() + 1);
        }
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_WITHER_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WITHER_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_WITHER_AMBIENT;
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public void rangedAttack(LivingEntity target, float charge) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setChargingAttack(boolean raiseHands) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean canTravelThroughPortals() {
        return this.canTravelThroughPortals;
    }
}
