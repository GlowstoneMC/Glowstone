package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

/**
 * A task that searches for and acquires targets for hostile mobs.
 * When a target is found, switches the entity to the TARGETING state.
 */
public class FindTargetTask extends EntityTask {

    private static final double DETECTION_RANGE = 16.0;
    private static final double VERTICAL_RANGE = 4.0;

    public FindTargetTask() {
        super("find_target", 5);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public int getDurationMin() {
        return 1;
    }

    @Override
    public int getDurationMax() {
        return 1;
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity instanceof Monster;
    }

    @Override
    public void start(GlowLivingEntity entity) {
    }

    @Override
    public void end(GlowLivingEntity entity) {
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (entity.getState() == HostileMobState.TARGETING) {
            return;
        }

        Player target = findNearestPlayer(entity);
        if (target != null) {
            entity.setState(HostileMobState.TARGETING);
        }
    }

    private Player findNearestPlayer(GlowLivingEntity entity) {
        Player nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (Entity nearby : entity.getNearbyEntities(DETECTION_RANGE, VERTICAL_RANGE, DETECTION_RANGE)) {
            if (!(nearby instanceof Player)) {
                continue;
            }
            Player player = (Player) nearby;
            if (!player.isOnline() || player.isDead()) {
                continue;
            }

            if (!canSee(entity, player)) {
                continue;
            }

            double distSq = entity.getLocation().distanceSquared(player.getLocation());
            if (distSq < nearestDistSq) {
                nearest = player;
                nearestDistSq = distSq;
            }
        }

        return nearest;
    }

    private boolean canSee(GlowLivingEntity entity, Player target) {
        return entity.hasLineOfSight(target);
    }
}
