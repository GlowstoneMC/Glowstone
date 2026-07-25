package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A task that causes an entity to wander randomly around its current location.
 */
public class WanderTask extends EntityTask {

    private static final double WANDER_RANGE = 8;
    private static final double SPEED = 0.15;
    private Location target;
    private int stuckTicks;

    public WanderTask() {
        super("wander");
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public int getDurationMin() {
        return TickUtil.secondsToTicks(2);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(6);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return ThreadLocalRandom.current().nextFloat() <= 0.05;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        target = getRandomWanderTarget(entity);
        stuckTicks = 0;
    }

    @Override
    public void end(GlowLivingEntity entity) {
        entity.setVelocity(new Vector(0, entity.getVelocity().getY(), 0));
        target = null;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (target == null) {
            return;
        }
        if (entity.getLocation().distanceSquared(target) < 1.0) {
            return;
        }
        Location location = entity.getLocation();
        double deltaX = target.getX() - location.getX();
        double deltaZ = target.getZ() - location.getZ();
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
        stuckTicks++;
        if (stuckTicks > 100) {
            target = getRandomWanderTarget(entity);
            stuckTicks = 0;
        }
    }

    private Location getRandomWanderTarget(GlowLivingEntity entity) {
        Location loc = entity.getLocation();
        double x = loc.getX() + (ThreadLocalRandom.current().nextDouble() - 0.5) * WANDER_RANGE * 2;
        double z = loc.getZ() + (ThreadLocalRandom.current().nextDouble() - 0.5) * WANDER_RANGE * 2;
        return new Location(loc.getWorld(), x, loc.getY(), z);
    }
}