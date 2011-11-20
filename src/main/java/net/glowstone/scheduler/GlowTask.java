package net.glowstone.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a task which is executed periodically.
 * @author Graham Edgecombe
 */
public class GlowTask implements BukkitTask {
    
    /**
     * The next task ID pending.
     */
    private static Integer nextTaskId = 0;
    
    /**
     * A lock to use when getting the next task ID.
     */
    private final static Object nextTaskIdLock = new Object();
    
    /**
     * The ID of this task.
     */
    private final int taskId;
    
    /**
     * The Runnable this task is representing.
     */
    private final Runnable task;
    
    /**
     * The Plugin that owns this task
     */
    private final Plugin owner;

    /**
     * The number of ticks before the call to the Runnable.
     */
    private final long delay;

    /**
     * The number of ticks between each call to the Runnable.
     */
    private final long period;

    /**
     * The current number of ticks since last initialization.
     */
    private long counter;

    /**
     * A flag which indicates if this task is running.
     */
    private boolean running = true;

    private final boolean sync;

    /**
     * Creates a new task with the specified number of ticks between
     * consecutive calls to {@link #execute()}.
     * @param ticks The number of ticks.
     */
    public GlowTask(Plugin owner, Runnable task, boolean sync, long delay, long period) {
        synchronized (nextTaskIdLock) {
            this.taskId = nextTaskId++;
        }
        this.owner = owner;
        this.task = task;
        this.delay = delay;
        this.period = period;
        this.counter = 0;
        this.sync = sync;
    }

    /**
     * Gets the ID of this task.
     */
    public int getTaskId() {
        return taskId;
    }

    public boolean isSync() {
        return sync;
    }

    public Plugin getOwner() {
        return owner;
    }

    /**
     * Stops this task.
     */
    public void stop() {
        running = false;
    }

    /**
     * Called every 'pulse' which is around 200ms in Minecraft. This method
     * updates the counters and calls {@link #execute()} if necessary.
     * @return The {@link #isRunning()} flag.
     */
    boolean pulse() {
        if (!running)
            return false;
        
        ++counter;
        if (counter >= delay) {
            if (period == -1) {
                task.run();
                running = false;
            } else if ((counter - delay) % period == 0) {
                task.run();
            }
        }
        
        return running;
    }

}
