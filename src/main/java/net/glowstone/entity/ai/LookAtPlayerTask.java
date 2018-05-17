package net.glowstone.entity.ai;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class LookAtPlayerTask extends EntityTask {

    private static final double RANGE = 10;
    private GlowPlayer target;
    private int delay = 1;

    public LookAtPlayerTask() {
        super("look_player");
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
        return TickUtil.secondsToTicks(6);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        EntityTask task = entity.getTaskManager().getTask("look_around");
        return task != null && !task.isExecuting()
            && ThreadLocalRandom.current().nextFloat() <= 0.025;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        target = null;
        List<Entity> nearbyEntities = entity.getNearbyEntities(RANGE, RANGE / 2, RANGE);
        double nearestSquared = Double.MAX_VALUE;
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity.getType() != EntityType.PLAYER) {
                continue;
            }
            double dist = nearbyEntity.getLocation().distanceSquared(entity.getLocation());
            if (dist < nearestSquared) {
                target = (GlowPlayer) nearbyEntity;
                nearestSquared = dist;
            }
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
        Location location = entity.getLocation();
        location.setPitch(0);
        location.setYaw(entity.getHeadYaw());
        entity.teleport(location);
        target = null;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (delay == 1) {
            delay = 0;
            return;
        }
        if (target == null || !target.isOnline()
            || entity.getLocation().distanceSquared(target.getLocation()) > (RANGE * RANGE)) {
            reset(entity);
            return;
        }
        Location other = target.getEyeLocation();
        Location location = entity.getLocation();
        double x = other.getX() - location.getX();
        double z = other.getZ() - location.getZ();
        float yaw = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90;
        entity.setHeadYaw(yaw); // todo: smooth head rotation (delta)
        // todo: pitch rotation (head up/down)
        delay = 1;

        if (entity.getType() == EntityType.ZOMBIE) {
            entity.setState(HostileMobState.TARGETING);
        }
    }
}
