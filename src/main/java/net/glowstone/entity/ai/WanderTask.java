package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Makes the entity wander around randomly within a radius.
 */
public class WanderTask extends EntityTask {

    private static final double RADIUS = 10.0;
    private int cooldown;

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
        return TickUtil.secondsToTicks(5);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity.getState() == MobState.WANDER
            && (--cooldown <= 0 || ThreadLocalRandom.current().nextFloat() <= 0.05);
    }

    @Override
    public void start(GlowLivingEntity entity) {
        Location loc = entity.getLocation();
        double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;
        double distance = 3 + ThreadLocalRandom.current().nextDouble() * (RADIUS - 3);
        double x = loc.getX() + Math.cos(angle) * distance;
        double z = loc.getZ() + Math.sin(angle) * distance;
        Location target = new Location(loc.getWorld(), x, loc.getY(), z);
        entity.setHeadYaw((float) Math.toDegrees(angle));
        TransportHelper.moveTowards(entity, target);
    }

    @Override
    public void end(GlowLivingEntity entity) {
        cooldown = ThreadLocalRandom.current().nextInt(60) + 60;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
    }
}
