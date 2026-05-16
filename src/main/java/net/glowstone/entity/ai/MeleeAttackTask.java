package net.glowstone.entity.ai;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import net.glowstone.util.pathfinding.Pathfinder;
import net.glowstone.util.pathfinding.algorithms.AStarAlgorithm;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A melee attack task that moves toward a target and attacks when in range.
 * Uses pathfinding to navigate to the target.
 */
public class MeleeAttackTask extends EntityTask {

    private static final double ATTACK_RANGE_SQ = 4.0;
    private static final int ATTACK_COOLDOWN_TICKS = 20;
    private static final double APPROACH_SPEED = 0.35;
    private static final int PATH_RECALC_TICKS = 10;
    private static final AStarAlgorithm ALGORITHM = new AStarAlgorithm();

    private LivingEntity target;
    private List<Vector> currentPath;
    private int pathIndex;
    private int ticksSincePathCalc;
    private int attackCooldown;

    public MeleeAttackTask() {
        super("melee_attack", 3);
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public int getDurationMin() {
        return TickUtil.secondsToTicks(5);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(10);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        return entity.getState() == HostileMobState.TARGETING && findTarget(entity) != null;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        target = findTarget(entity);
        if (target != null) {
            calculatePath(entity);
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
        currentPath = null;
        pathIndex = 0;
        ticksSincePathCalc = 0;
        attackCooldown = 0;
        target = null;
        entity.setMovement(new Vector(0, 0, 0));
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (target == null || target.isDead()) {
            target = findTarget(entity);
            if (target == null) {
                reset(entity);
                return;
            }
        }

        if (target instanceof Player && !((Player) target).isOnline()) {
            reset(entity);
            return;
        }

        Location entityLoc = entity.getLocation();
        Location targetLoc = target.getLocation();
        double distSq = entityLoc.distanceSquared(targetLoc);

        if (distSq <= ATTACK_RANGE_SQ) {
            if (attackCooldown <= 0) {
                performAttack(entity, target);
                attackCooldown = ATTACK_COOLDOWN_TICKS;
            }
            lookAt(entity, targetLoc);
            return;
        }

        ticksSincePathCalc++;
        if (currentPath == null || ticksSincePathCalc >= PATH_RECALC_TICKS) {
            calculatePath(entity);
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
            moveToward(entity, targetLoc.toVector());
        }
    }

    private LivingEntity findTarget(GlowLivingEntity entity) {
        double range = 16.0;
        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (Entity nearby : entity.getNearbyEntities(range, range / 2, range)) {
            if (!(nearby instanceof LivingEntity)) {
                continue;
            }
            if (nearby instanceof Player) {
                Player player = (Player) nearby;
                if (!player.isOnline() || player.isDead()) {
                    continue;
                }
            }
            if (nearby.equals(entity)) {
                continue;
            }

            double distSq = entity.getLocation().distanceSquared(nearby.getLocation());
            if (distSq < nearestDistSq) {
                nearest = (LivingEntity) nearby;
                nearestDistSq = distSq;
            }
        }

        return nearest;
    }

    private void performAttack(GlowLivingEntity attacker, LivingEntity target) {
        double damage = 2.0;
        AttributeInstance attackDamage = attacker.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attackDamage != null) {
            damage = attackDamage.getValue();
        }

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
            attacker, target, DamageCause.ENTITY_ATTACK, damage);
        target.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            target.damage(event.getFinalDamage(), attacker);
        }
    }

    private void calculatePath(GlowLivingEntity entity) {
        if (target == null) {
            return;
        }
        GlowBlock startBlock = (GlowBlock) entity.getLocation().getBlock();
        GlowBlock endBlock = (GlowBlock) target.getLocation().getBlock();

        Pathfinder pathfinder = new Pathfinder(startBlock, endBlock,
            Material.LAVA, Material.FIRE, Material.CACTUS);
        currentPath = pathfinder.getPath(ALGORITHM);
        pathIndex = 0;
    }

    private void lookAt(GlowLivingEntity entity, Location target) {
        Location location = entity.getLocation();
        double deltaX = target.getX() - location.getX();
        double deltaZ = target.getZ() - location.getZ();
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
        entity.setHeadYaw(yaw);
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
        entity.setSpeed(APPROACH_SPEED);
        entity.setMovement(new Vector(deltaX, 0, deltaZ));
    }
}
