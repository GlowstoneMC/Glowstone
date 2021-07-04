package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.TickUtil;

import java.util.concurrent.ThreadLocalRandom;

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
        return TickUtil.secondsToTicks(2);
    }

    @Override
    public int getDurationMax() {
        return TickUtil.secondsToTicks(4);
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
