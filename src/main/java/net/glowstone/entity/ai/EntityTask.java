package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;

import java.util.Random;

public abstract class EntityTask<T extends GlowLivingEntity> {

    protected static final Random random = new Random();

    private final String name;
    private boolean executing = false;
    private int duration = 0;
    private boolean paused = false;

    public EntityTask(String name) {
        this.name = name;
    }

    public final void pulse(T entity) {
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
            duration = getDurationMin() == getDurationMax() ? getDurationMin() : random.nextInt(getDurationMax() - getDurationMin()) + getDurationMin();
            executing = true;
            start(entity);
            return;
        }
    }

    /**
     * Resets the progress of this task for this entity.
     *
     * @param entity the entity in question.
     */
    public void reset(T entity) {
        end(entity);
        duration = 0;
        executing = false;
    }

    /**
     * Whether this task is currently being executed.
     *
     * @return true if this task is being executed, false otherwise.
     */
    public final boolean isExecuting() {
        return executing;
    }

    /**
     * Whether this task is paused.
     *
     * @return whether this task is paused.
     */
    public final boolean isPaused() {
        return paused;
    }

    /**
     * Resumes the previously paused task for this entity.
     *
     * @param entity the entity in question.
     */
    public final void resume(T entity) {
        if (!isPaused()) return;
        paused = false;
    }

    /**
     * Pauses this task for this entity.
     *
     * @param entity the entity in question.
     */
    public final void pause(T entity) {
        if (isPaused()) return;
        reset(entity);
        paused = true;
    }

    /**
     * The name of this EntityTask. Must be unique to each EntityTask implementation.
     *
     * @return the name of this EntityTask.
     */
    public String getName() {
        return name;
    }

    /**
     * The minimum duration of this task.
     * This value is ignored if this task is instant.
     *
     * @return the minimum duration of this task, in ticks.
     */
    public abstract int getDurationMin();

    /**
     * The maximum duration of this task.
     * This value is ignored if this task is instant.
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
    public abstract boolean shouldStart(T entity);

    /**
     * Invoked when this task is about to start for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void start(T entity);

    /**
     * Invoked when this task is being ended for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void end(T entity);

    /**
     * Invoked each tick when this task is being executed for this entity.
     *
     * @param entity the entity in question.
     */
    public abstract void execute(T entity);

    /**
     * Whether this task is instant.
     * An "instant" task will be executed every tick while the entity is alive.
     *
     * @return the entity in question.
     */
    public abstract boolean isInstant();
}
