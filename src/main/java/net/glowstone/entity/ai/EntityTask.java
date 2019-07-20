package net.glowstone.entity.ai;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import net.glowstone.entity.GlowLivingEntity;
import org.jetbrains.annotations.NonNls;

public abstract class EntityTask implements Comparable<EntityTask> {

    /**
     * The name of this EntityTask. Must be unique to each EntityTask implementation.
     *
     * @return the name of this EntityTask.
     */
    @Getter
    @NonNls
    private final String name;
    /**
     * Whether this task is currently being executed.
     *
     * @return true if this task is being executed, false otherwise.
     */
    @Getter
    private boolean executing = false;
    private int duration = 0;
    /**
     * Whether this task is paused.
     *
     * @return whether this task is paused.
     */
    @Getter
    private boolean paused = false;

    public EntityTask(@NonNls String name) {
        this.name = name;
    }

    @Override
    public int compareTo(EntityTask other) {
        return 0; // TODO: AI task priority
    }

    /**
     * Advances this task by a tick on the given entity.
     *
     * @param entity the entity to run on
     */
    public final void pulse(GlowLivingEntity entity) {
        if (paused || entity.isDead() || !entity.hasAI()) {
            return;
        }
        if (isInstant()) {
            execute(entity);
            return;
        }
        if (executing && duration > 0) {
            duration--;
            execute(entity);
            return;
        }
        if (executing && duration == 0) {
            executing = false;
            end(entity);
            return;
        }
        if (!executing && shouldStart(entity)) {
            duration = getDurationMin() == getDurationMax() ? getDurationMin()
                : ThreadLocalRandom.current().nextInt(getDurationMax() - getDurationMin())
                    + getDurationMin();
            executing = true;
            start(entity);
        }
    }

    /**
     * Resets the progress of this task for this entity.
     *
     * @param entity the entity in question.
     */
    public void reset(GlowLivingEntity entity) {
        end(entity);
        duration = 0;
        executing = false;
    }

    /**
     * Resumes the previously paused task for this entity.
     *
     * @param entity the entity in question.
     */
    public final void resume(GlowLivingEntity entity) {
        if (!isPaused()) {
            return;
        }
        paused = false;
    }

    /**
     * Pauses this task for this entity.
     *
     * @param entity the entity in question.
     */
    public final void pause(GlowLivingEntity entity) {
        if (isPaused()) {
            return;
        }
        reset(entity);
        paused = true;
    }

    /**
     * The minimum duration of this task. This value is ignored if this task is instant.
     *
     * @return the minimum duration of this task, in ticks.
     */
    public abstract int getDurationMin();

    /**
     * The maximum duration of this task. This value is ignored if this task is instant.
     *
     * @return the maximum duration of this task, in ticks.
     */
    public abstract int getDurationMax();

    /**
     * Whether the task should begin executing for this entity.
     *
     * @param entity the entity in question.
     * @return true if the task should start, false otherwise.
     */
    public abstract boolean shouldStart(GlowLivingEntity entity);

    /**
     * Invoked when this task is about to start for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void start(GlowLivingEntity entity);

    /**
     * Invoked when this task is being ended for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void end(GlowLivingEntity entity);

    /**
     * Invoked each tick when this task is being executed for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void execute(GlowLivingEntity entity);

    /**
     * Whether this task is instant. An "instant" task will be executed every tick while the entity
     * is alive.
     *
     * @return the entity in question.
     */
    public abstract boolean isInstant();
}
