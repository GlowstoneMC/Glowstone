package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;

import java.util.ArrayList;
import java.util.Objects;

public class TaskManager {

    private final GlowLivingEntity entity;
    private final ArrayList<EntityTask> tasks;

    public TaskManager(GlowLivingEntity entity) {
        this.entity = entity;
        this.tasks = new ArrayList<>();
    }

    public EntityTask getTask(String name) {
        for (EntityTask task : tasks) {
            if (task != null && Objects.equals(task.getName(), name)) {
                return task;
            }
        }
        return null;
    }

    public EntityTask getTask(Class<? extends EntityTask> clazz) {
        for (EntityTask task : tasks) {
            if (Objects.equals(task.getClass(), clazz)) {
                return task;
            }
        }
        return null;
    }

    public EntityTask getNewTask(String name) {
        Class<? extends EntityTask> clazz = EntityDirector.getEntityTask(name);
        try {
            if (clazz != null) {
                return clazz.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateState() {
        cancelTasks();
        for (String task : EntityDirector.getEntityMobStateTask(entity.getType(), entity.getState())) {
            addTask(task);
        }
    }

    public void cancel(EntityTask task) {
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

    public void addTask(EntityTask task) {
        if (task != null) {
            if (getTask(task.getName()) != null) {
                cancel(getTask(task.getName()));
            }
            tasks.add(task);
        }
    }

    public void addTask(String task) {
        if (getTask(task) != null) {
            cancel(getTask(task));
        }
        tasks.add(getNewTask(task));
    }
}
