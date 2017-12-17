package net.glowstone.entity.ai;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowLivingEntity;

public class LookAroundTask extends EntityTask {

    private int delay = ThreadLocalRandom.current().nextInt(10) + 15;

    public LookAroundTask() {
        super("look_around");
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public int getDurationMin() {
        return 2 * 20;
    }

    @Override
    public int getDurationMax() {
        return 4 * 20;
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        EntityTask task = entity.getTaskManager().getTask("look_player");
        return (task == null || !task.isExecuting())
            && ThreadLocalRandom.current().nextFloat() <= 0.1;
    }

    @Override
    public void start(GlowLivingEntity entity) {
    }

    @Override
    public void end(GlowLivingEntity entity) {
        delay = ThreadLocalRandom.current().nextInt(20) + 60;
    }

    @Override
    public void execute(GlowLivingEntity entity) {
        if (ThreadLocalRandom.current().nextFloat() <= 0.15 && delay == 0) {
            entity.setHeadYaw(entity.getHeadYaw() + ThreadLocalRandom.current().nextFloat() * 179
                - 90); // todo: smooth
            delay = 20;
        }
        if (delay > 0) {
            delay--;
        }
    }
}
