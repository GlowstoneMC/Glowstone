package net.glowstone.entity.monster;

import net.glowstone.Explosion;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityDamageEvent;

public class GlowWither extends GlowMonster implements Wither {

    private int invulnerableTicks;
    private Entity centerTarget, leftTarget, rightTarget;

    public GlowWither(Location loc) {
        super(loc, EntityType.WITHER, 300);
        setInvulnerableTicks(220);
        setCenterTarget(null);
        setLeftTarget(null);
        setRightTarget(null);
        setHealth(getMaxHealth() / 3);
    }

    @Override
    public void damage(double amount, Entity source, EntityDamageEvent.DamageCause cause) {
        if (invulnerableTicks > 0) {
            return;
        }
        super.damage(amount, source, cause);
    }

    public int getInvulnerableTicks() {
        return invulnerableTicks;
    }

    public void setInvulnerableTicks(int invulnerableTicks) {
        this.invulnerableTicks = invulnerableTicks;
        this.metadata.set(MetadataIndex.WITHER_INVULN_TIME, invulnerableTicks);
    }

    public Entity getCenterTarget() {
        return centerTarget;
    }

    public void setCenterTarget(Entity centerTarget) {
        this.centerTarget = centerTarget;
        this.metadata.set(MetadataIndex.WITHER_TARGET_1, centerTarget == null ? 0 : centerTarget.getEntityId());
    }

    public Entity getLeftTarget() {
        return leftTarget;
    }

    public void setLeftTarget(Entity leftTarget) {
        this.leftTarget = leftTarget;
        this.metadata.set(MetadataIndex.WITHER_TARGET_2, leftTarget == null ? 0 : leftTarget.getEntityId());
    }

    public Entity getRightTarget() {
        return rightTarget;
    }

    public void setRightTarget(Entity rightTarget) {
        this.rightTarget = rightTarget;
        this.metadata.set(MetadataIndex.WITHER_TARGET_3, rightTarget == null ? 0 : rightTarget.getEntityId());
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
}
