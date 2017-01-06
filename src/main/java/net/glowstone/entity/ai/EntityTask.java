package net.glowstone.entity.ai;

import net.glowstone.entity.GlowEntity;

import java.util.Random;

public abstract class EntityTask<T extends GlowEntity> {

    private final String name;
    private boolean executing = false;
    private int duration = 0;
    protected final Random random;

    public EntityTask(String name) {
        this.name = name;
        this.random = new Random();
    }

    public abstract boolean isInstant();

    public final void pulse(T entity) {
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
            execute(entity);
            end(entity);
            return;
        }
        if (!executing && shouldStart()) {
            duration = getDurationMin() == getDurationMax() ? getDurationMin() : random.nextInt(getDurationMax() - getDurationMin()) + getDurationMin();
            executing = true;
            start(entity);
            return;
        }
    }

    public void reset(T entity) {
        end(entity);
        duration = 0;
        executing = false;
    }

    public String getName() {
        return name;
    }

    public abstract int getDurationMin();

    public abstract int getDurationMax();

    public abstract boolean shouldStart();

    public abstract void start(T entity);

    public abstract void end(T entity);

    public abstract void execute(T entity);
}
