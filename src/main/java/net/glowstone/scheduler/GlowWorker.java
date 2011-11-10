package net.glowstone.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitWorker;

public class GlowWorker implements BukkitWorker, Runnable {
    private final int id;
    private final Plugin owner;
    private final GlowTask task;
    private Thread thread = null;
    private boolean shouldContinue = true;

    protected GlowWorker(final GlowTask task, final GlowScheduler scheduler) {
        this.id = task.getTaskId();
        this.owner = task.getOwner();
        this.task = task;
        this.thread = new Thread(new Runnable() {
            public void run() {
                task.pulse();
                scheduler.workerComplete(GlowWorker.this);
            }
        });
        thread.start();
    }

    public int getTaskId() {
        return id;
    }

    public Plugin getOwner() {
        return owner;
    }

    public Thread getThread() {
        return thread;
    }

    public GlowTask getTask() {
        return task;
    }

    public boolean shouldContinue() {
        return shouldContinue;
    }

    public void cancel() {
        if (thread == null) return;
        if (!thread.isAlive()) {
            thread.interrupt();
            return;
        }
        task.stop();
    }

    public void run() {

        shouldContinue = task.pulse();
    }
}
