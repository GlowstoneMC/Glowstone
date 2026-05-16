package net.glowstone.entity.ai;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import net.glowstone.util.pathfinding.Pathfinder;
import net.glowstone.util.pathfinding.algorithms.AStarAlgorithm;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A panic task that causes entities to flee rapidly when in the ATTACKED state.
 * Uses pathfinding to find an escape route away from danger.
 */
public class PanicTask extends EntityTask {

    private static final int PANIC_RADIUS = 16;
    private static final double PANIC_SPEED = 0.5;
    private static final AStarAlgorithm ALGORITHM = new AStarAlgorithm();

    private List<Vector> currentPath;
    private int pathIndex;
    private Location fleeTarget;

    public PanicTask() {
        super("panic", 1);
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
        return TickUtil.secondsToTicks(4);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity.getState() == MobState.ATTACKED;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        fleeTarget = findFleeLocation(entity);
        if (fleeTarget != null) {
            calculatePath(entity);
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
        currentPath = null;
        pathIndex = 0;
        fleeTarget = null;
        entity.setMovement(new Vector(0, 0, 0));
        if (entity.getState() == MobState.ATTACKED) {
            entity.setState(MobState.IDLE);
        }
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (fleeTarget == null || currentPath == null || currentPath.isEmpty()) {
            reset(entity);
            return;
        }

        if (pathIndex >= currentPath.size()) {
            reset(entity);
            return;
        }

        Vector nextPoint = currentPath.get(pathIndex);
        Location entityLoc = entity.getLocation();
        double distSq = entityLoc.toVector().distanceSquared(nextPoint);

        if (distSq < 1.0) {
            pathIndex++;
            if (pathIndex >= currentPath.size()) {
                reset(entity);
                return;
            }
            nextPoint = currentPath.get(pathIndex);
        }

        moveToward(entity, nextPoint);
    }

    private Location findFleeLocation(GlowLivingEntity entity) {
        Location origin = entity.getLocation();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int attempts = 0; attempts < 10; attempts++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = random.nextInt(PANIC_RADIUS / 2, PANIC_RADIUS);
            int dx = (int) (Math.cos(angle) * distance);
            int dz = (int) (Math.sin(angle) * distance);
            int dy = random.nextInt(-3, 4);

            Location target = origin.clone().add(dx, dy, dz);
            if (isValidFleeTarget(target)) {
                return target;
            }
        }
        return null;
    }

    private boolean isValidFleeTarget(Location loc) {
        if (loc.getWorld() == null) {
            return false;
        }
        Material blockType = loc.getBlock().getType();
        Material belowType = loc.clone().subtract(0, 1, 0).getBlock().getType();

        return !blockType.isSolid()
            && belowType.isSolid()
            && blockType != Material.WATER
            && blockType != Material.LAVA
            && blockType != Material.FIRE;
    }

    private void calculatePath(GlowLivingEntity entity) {
        if (fleeTarget == null) {
            return;
        }
        GlowBlock startBlock = (GlowBlock) entity.getLocation().getBlock();
        GlowBlock endBlock = (GlowBlock) fleeTarget.getBlock();

        Pathfinder pathfinder = new Pathfinder(startBlock, endBlock,
            Material.LAVA, Material.FIRE, Material.CACTUS);
        currentPath = pathfinder.getPath(ALGORITHM);
        pathIndex = 0;
    }

    private void moveToward(GlowLivingEntity entity, Vector target) {
        Location location = entity.getLocation();
        double deltaX = target.getX() - location.getX();
        double deltaZ = target.getZ() - location.getZ();

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (distance > 0) {
            deltaX /= distance;
            deltaZ /= distance;
        }

        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
        entity.setHeadYaw(yaw);
        entity.setSpeed(PANIC_SPEED);
        entity.setMovement(new Vector(deltaX, 0, deltaZ));
    }
}
