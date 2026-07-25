package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A task that causes a hostile entity to attack nearby players.
 */
public class MeleeAttackTask extends EntityTask {

    private static final double RANGE = 16;
    private static final double ATTACK_RANGE = 2.5;
    private static final double SPEED = 0.35;
    private GlowPlayer target;
    private int attackCooldown;

    public MeleeAttackTask() {
        super("melee_attack");
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public int getDurationMin() {
        return TickUtil.secondsToTicks(4);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(10);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity.getState() == HostileMobState.TARGETING;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        target = findNearestPlayer(entity);
        attackCooldown = 0;
    }

    @Override
    public void end(GlowLivingEntity entity) {
        entity.setVelocity(new Vector(0, entity.getVelocity().getY(), 0));
        target = null;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (target == null || !target.isOnline() || target.isDead()) {
            target = findNearestPlayer(entity);
            if (target == null) {
                entity.setState(MobState.IDLE);
                return;
            }
        }
        double distanceSq = entity.getLocation().distanceSquared(target.getLocation());
        if (distanceSq > RANGE * RANGE) {
            entity.setState(MobState.IDLE);
            return;
        }
        if (distanceSq > ATTACK_RANGE * ATTACK_RANGE) {
            Location location = entity.getLocation();
            Location targetLoc = target.getLocation();
            double deltaX = targetLoc.getX() - location.getX();
            double deltaZ = targetLoc.getZ() - location.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            entity.setVelocity(new Vector(
                (deltaX / distance) * SPEED,
                entity.getVelocity().getY(),
                (deltaZ / distance) * SPEED
            ));
            float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
            entity.setHeadYaw(yaw);
            entity.setYaw(yaw);
        } else {
            if (attackCooldown <= 0) {
                target.damage(3.0, entity);
                attackCooldown = 20;
            } else {
                attackCooldown--;
            }
        }
    }

    private GlowPlayer findNearestPlayer(GlowLivingEntity entity) {
        List<Entity> nearbyEntities = entity.getNearbyEntities(RANGE, RANGE, RANGE);
        double nearestSquared = Double.MAX_VALUE;
        GlowPlayer nearest = null;
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity.getType() != EntityType.PLAYER) {
                continue;
            }
            GlowPlayer player = (GlowPlayer) nearbyEntity;
            if (player.isDead() || !player.isOnline()) {
                continue;
            }
            double dist = player.getLocation().distanceSquared(entity.getLocation());
            if (dist < nearestSquared) {
                nearest = player;
                nearestSquared = dist;
            }
        }
        return nearest;
    }
}