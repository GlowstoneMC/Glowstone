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
 * A task that causes a passive entity to flee from nearby players when threatened.
 */
public class FleeTask extends EntityTask {

    private static final double FLEE_RANGE = 8;
    private static final double SAFE_DISTANCE = 12;
    private static final double SPEED = 0.25;
    private GlowPlayer threat;

    public FleeTask() {
        super("flee");
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public int getDurationMin() {
        return TickUtil.secondsToTicks(3);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(6);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity.getState() == MobState.ATTACKED;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        threat = findNearestPlayer(entity);
    }

    @Override
    public void end(GlowLivingEntity entity) {
        entity.setVelocity(new Vector(0, entity.getVelocity().getY(), 0));
        threat = null;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (threat == null || !threat.isOnline() || threat.isDead()) {
            threat = findNearestPlayer(entity);
            if (threat == null) {
                entity.setState(MobState.IDLE);
                return;
            }
        }
        double distanceSq = entity.getLocation().distanceSquared(threat.getLocation());
        if (distanceSq > SAFE_DISTANCE * SAFE_DISTANCE) {
            entity.setState(MobState.IDLE);
            return;
        }
        Location location = entity.getLocation();
        Location threatLoc = threat.getLocation();
        double deltaX = location.getX() - threatLoc.getX();
        double deltaZ = location.getZ() - threatLoc.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (distance < 0.1) {
            return;
        }
        entity.setVelocity(new Vector(
            (deltaX / distance) * SPEED,
            entity.getVelocity().getY(),
            (deltaZ / distance) * SPEED
        ));
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
        entity.setHeadYaw(yaw);
        entity.setYaw(yaw);
    }

    private GlowPlayer findNearestPlayer(GlowLivingEntity entity) {
        List<Entity> nearbyEntities = entity.getNearbyEntities(FLEE_RANGE, FLEE_RANGE, FLEE_RANGE);
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