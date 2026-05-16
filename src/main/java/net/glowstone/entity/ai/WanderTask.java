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
 * A task that makes entities wander randomly around the world.
 * Uses pathfinding to navigate terrain and obstacles.
 */
public class WanderTask extends EntityTask {

    private static final int WANDER_RADIUS = 10;
    private static final int VERTICAL_RADIUS = 3;
    private static final double MOVEMENT_SPEED = 0.2;
    private static final int PATH_RECALC_TICKS = 20;
    private static final AStarAlgorithm ALGORITHM = new AStarAlgorithm();

    private List<Vector> currentPath;
    private int pathIndex;
    private int ticksSincePathCalc;
    private Location targetLocation;

    public WanderTask() {
        super("wander", 8);
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
        return TickUtil.secondsToTicks(8);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        if (entity.getState() != MobState.IDLE && entity.getState() != MobState.WANDER) {
            return false;
        }
        EntityTask followTask = entity.getTaskManager().getTask("follow_player");
        if (followTask != null && followTask.isExecuting()) {
            return false;
        }
        return ThreadLocalRandom.current().nextFloat() <= 0.02;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        targetLocation = findRandomTarget(entity);
        if (targetLocation != null) {
            calculatePath(entity);
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
        currentPath = null;
        pathIndex = 0;
        ticksSincePathCalc = 0;
        targetLocation = null;
        entity.setMovement(new Vector(0, 0, 0));
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (targetLocation == null || currentPath == null || currentPath.isEmpty()) {
            reset(entity);
            return;
        }

        ticksSincePathCalc++;
        if (ticksSincePathCalc >= PATH_RECALC_TICKS) {
            calculatePath(entity);
            ticksSincePathCalc = 0;
        }

        if (pathIndex >= currentPath.size()) {
            reset(entity);
            return;
        }

        Vector nextPoint = currentPath.get(pathIndex);
        Location entityLoc = entity.getLocation();
        double distSq = entityLoc.toVector().distanceSquared(nextPoint);

        if (distSq < 0.5) {
            pathIndex++;
            if (pathIndex >= currentPath.size()) {
                reset(entity);
                return;
            }
            nextPoint = currentPath.get(pathIndex);
        }

        moveToward(entity, nextPoint);
    }

    private Location findRandomTarget(GlowLivingEntity entity) {
        Location origin = entity.getLocation();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int attempts = 0; attempts < 10; attempts++) {
            int dx = random.nextInt(-WANDER_RADIUS, WANDER_RADIUS + 1);
            int dz = random.nextInt(-WANDER_RADIUS, WANDER_RADIUS + 1);
            int dy = random.nextInt(-VERTICAL_RADIUS, VERTICAL_RADIUS + 1);

            Location target = origin.clone().add(dx, dy, dz);
            if (isValidTarget(target)) {
                return target;
            }
        }
        return null;
    }

    private boolean isValidTarget(Location loc) {
        if (loc.getWorld() == null) {
            return false;
        }
        Material blockType = loc.getBlock().getType();
        Material belowType = loc.clone().subtract(0, 1, 0).getBlock().getType();

        return !blockType.isSolid()
            && belowType.isSolid()
            && blockType != Material.LAVA
            && blockType != Material.FIRE;
    }

    private void calculatePath(GlowLivingEntity entity) {
        if (targetLocation == null) {
            return;
        }
        GlowBlock startBlock = (GlowBlock) entity.getLocation().getBlock();
        GlowBlock endBlock = (GlowBlock) targetLocation.getBlock();

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
        entity.setSpeed(MOVEMENT_SPEED);
        entity.setMovement(new Vector(deltaX, 0, deltaZ));
    }
}
