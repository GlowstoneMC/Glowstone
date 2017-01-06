package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;

import java.util.ArrayList;

public final class TaskManager {

    private final GlowLivingEntity entity;
    private final ArrayList<EntityTask<GlowLivingEntity>> tasks;

    public TaskManager(GlowLivingEntity entity) {
        this.entity = entity;
        this.tasks = new ArrayList<>();
    }

    public EntityTask<GlowLivingEntity> getTask(String name) {
        for (EntityTask<GlowLivingEntity> task : tasks) {
            if (task.getName().equals(name)) {
                return task;
            }
        }
        return null;
    }

    public EntityTask<GlowLivingEntity> getTask(Class<? extends EntityTask<GlowLivingEntity>> clazz) {
        for (EntityTask<GlowLivingEntity> task : tasks) {
            if (task.getClass().equals(clazz)) {
                return task;
            }
        }
        return null;
    }

    public void cancel(EntityTask<GlowLivingEntity> task) {
        task.reset(entity);
        tasks.remove(task);
    }

    public void cancelTasks() {
        tasks.forEach(task -> task.reset(entity));
        tasks.clear();
    }

    public void pulse() {
        tasks.forEach(task -> task.pulse(entity));
    }

    public void addTask(EntityTask<GlowLivingEntity> task) {
        if (getTask(task.getName()) != null) {
            cancel(getTask(task.getName()));
        }
        tasks.add(task);
    }
}
