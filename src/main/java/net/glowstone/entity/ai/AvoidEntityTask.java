package net.glowstone.entity.ai;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import net.glowstone.util.pathfinding.Pathfinder;
import net.glowstone.util.pathfinding.algorithms.AStarAlgorithm;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A task that makes passive entities flee from threats like monsters or players.
 * Uses pathfinding to navigate away from danger.
 */
public class AvoidEntityTask extends EntityTask {

    private static final double DETECTION_RANGE = 8.0;
    private static final double SAFE_DISTANCE_SQ = 100.0;
    private static final double FLEE_SPEED = 0.4;
    private static final int PATH_RECALC_TICKS = 10;
    private static final AStarAlgorithm ALGORITHM = new AStarAlgorithm();

    private LivingEntity threat;
    private List<Vector> currentPath;
    private int pathIndex;
    private int ticksSincePathCalc;

    public AvoidEntityTask() {
        super("avoid_entity", 2);
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
        if (entity instanceof Monster) {
            return false;
        }
        return findThreat(entity) != null;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        threat = findThreat(entity);
        if (threat != null) {
            calculateFleePath(entity);
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
        currentPath = null;
        pathIndex = 0;
        ticksSincePathCalc = 0;
        threat = null;
        entity.setMovement(new Vector(0, 0, 0));
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (threat == null || threat.isDead()) {
            threat = findThreat(entity);
            if (threat == null) {
                reset(entity);
                return;
            }
            calculateFleePath(entity);
        }

        Location entityLoc = entity.getLocation();
        Location threatLoc = threat.getLocation();
        double distSq = entityLoc.distanceSquared(threatLoc);

        if (distSq >= SAFE_DISTANCE_SQ) {
            reset(entity);
            return;
        }

        ticksSincePathCalc++;
        if (ticksSincePathCalc >= PATH_RECALC_TICKS) {
            calculateFleePath(entity);
            ticksSincePathCalc = 0;
        }

        if (currentPath != null && !currentPath.isEmpty() && pathIndex < currentPath.size()) {
            Vector nextPoint = currentPath.get(pathIndex);
            double pointDistSq = entityLoc.toVector().distanceSquared(nextPoint);

            if (pointDistSq < 1.0) {
                pathIndex++;
            }

            if (pathIndex < currentPath.size()) {
                moveToward(entity, currentPath.get(pathIndex));
            }
        } else {
            fleeDirectlyFrom(entity, threatLoc);
        }
    }

    private LivingEntity findThreat(GlowLivingEntity entity) {
        for (Entity nearby : entity.getNearbyEntities(DETECTION_RANGE, DETECTION_RANGE / 2, DETECTION_RANGE)) {
            if (nearby instanceof Monster) {
                return (LivingEntity) nearby;
            }
            if (nearby instanceof Player) {
                Player player = (Player) nearby;
                if (player.isOnline() && !player.isDead()) {
                    if (player.isSprinting()) {
                        return player;
                    }
                }
            }
        }
        return null;
    }

    private void calculateFleePath(GlowLivingEntity entity) {
        if (threat == null) {
            return;
        }

        Location entityLoc = entity.getLocation();
        Location threatLoc = threat.getLocation();

        double dx = entityLoc.getX() - threatLoc.getX();
        double dz = entityLoc.getZ() - threatLoc.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            dx = dx / distance * 10;
            dz = dz / distance * 10;
        }

        Location fleeTarget = entityLoc.clone().add(dx, 0, dz);
        fleeTarget = findValidLocation(fleeTarget);

        if (fleeTarget != null) {
            GlowBlock startBlock = (GlowBlock) entityLoc.getBlock();
            GlowBlock endBlock = (GlowBlock) fleeTarget.getBlock();

            Pathfinder pathfinder = new Pathfinder(startBlock, endBlock,
                Material.LAVA, Material.FIRE, Material.CACTUS);
            currentPath = pathfinder.getPath(ALGORITHM);
            pathIndex = 0;
        }
    }

    private Location findValidLocation(Location target) {
        if (target.getWorld() == null) {
            return null;
        }

        for (int dy = 0; dy <= 3; dy++) {
            Location check = target.clone().add(0, -dy, 0);
            if (isValidLocation(check)) {
                return check;
            }
            if (dy > 0) {
                check = target.clone().add(0, dy, 0);
                if (isValidLocation(check)) {
                    return check;
                }
            }
        }
        return null;
    }

    private boolean isValidLocation(Location loc) {
        Material blockType = loc.getBlock().getType();
        Material belowType = loc.clone().subtract(0, 1, 0).getBlock().getType();
        return !blockType.isSolid() && belowType.isSolid();
    }

    private void fleeDirectlyFrom(GlowLivingEntity entity, Location threatLoc) {
        Location entityLoc = entity.getLocation();
        double dx = entityLoc.getX() - threatLoc.getX();
        double dz = entityLoc.getZ() - threatLoc.getZ();

        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 0) {
            dx /= distance;
            dz /= distance;
        }

        float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90;
        entity.setHeadYaw(yaw);
        entity.setSpeed(FLEE_SPEED);
        entity.setMovement(new Vector(dx, 0, dz));
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
        entity.setSpeed(FLEE_SPEED);
        entity.setMovement(new Vector(deltaX, 0, deltaZ));
    }
}
