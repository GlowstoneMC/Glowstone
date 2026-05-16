package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A task that allows entities to swim and stay afloat in water.
 * This is a high-priority instant task that activates whenever the entity is in water.
 */
public class SwimTask extends EntityTask {

    private static final double SWIM_SPEED = 0.04;

    public SwimTask() {
        super("swim", 0);
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
        return true;
    }

    @Override
    public void start(GlowLivingEntity entity) {
    }

    @Override
    public void end(GlowLivingEntity entity) {
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        Block block = entity.getLocation().getBlock();
        Material material = block.getType();

        if (material == Material.WATER || material == Material.LAVA) {
            if (ThreadLocalRandom.current().nextFloat() < 0.8) {
                Vector velocity = entity.getVelocity();
                velocity.setY(velocity.getY() + SWIM_SPEED);
                entity.setVelocity(velocity);
            }
        }
    }
}
