package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Attacks the entity's target with melee damage when in range.
 */
public class MeleeAttackTask extends EntityTask {

    private static final double ATTACK_RANGE = 1.5;
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
        return TickUtil.secondsToTicks(1);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(2);
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        LivingEntity target = entity.getTarget();
        if (target == null || target.isDead()) {
            return false;
        }
        double dist = entity.getLocation().distanceSquared(target.getLocation());
        return dist <= (ATTACK_RANGE * ATTACK_RANGE) * 1.5;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        attackCooldown = 0;
    }

    @Override
    public void end(GlowLivingEntity entity) {
        attackCooldown = 10;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        LivingEntity target = entity.getTarget();
        if (target == null || target.isDead()) {
            reset(entity);
            return;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }
        double dist = entity.getLocation().distanceSquared(target.getLocation());
        if (dist > (ATTACK_RANGE * ATTACK_RANGE)) {
            TransportHelper.moveTowards(entity, target.getLocation());
            return;
        }
        Location diff = target.getLocation().subtract(entity.getLocation());
        entity.setHeadYaw(diff.getYaw());
        target.damage(2.0 + ThreadLocalRandom.current().nextDouble() * 2.0, entity);
        attackCooldown = 20;
    }
}
