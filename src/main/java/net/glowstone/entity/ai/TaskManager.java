package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;

import java.util.ArrayList;
import java.util.Objects;

public class TaskManager {

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
        return getTask(EntityDirector.getEntityTask(name));
    }

    public EntityTask<GlowLivingEntity> getTask(Class<? extends EntityTask> clazz) {
        for (EntityTask<GlowLivingEntity> task : tasks) {
            if (task.getClass().equals(clazz)) {
                return task;
            }
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateState() {
        addTask(getTask(Objects.requireNonNull(EntityDirector.getEntityMobStateTask(entity.getType(), entity.getState()))));
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
