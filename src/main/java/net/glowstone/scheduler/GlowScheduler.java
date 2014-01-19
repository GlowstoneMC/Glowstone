package net.glowstone.scheduler;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * A class which schedules {@link GlowTask}s.
 * @author Graham Edgecombe
 */
public final class GlowScheduler implements BukkitScheduler {

    /**
     * The number of milliseconds between pulses.
     */
    private static final int PULSE_EVERY = 50;
    
    /**
     * The server this scheduler is managing for.
     */
    private final GlowServer server;

    /**
     * The scheduled executor service which backs this scheduler.
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * A list of new tasks to be added.
     */
    private final List<GlowTask> newTasks = new ArrayList<GlowTask>();

    /**
     * A list of tasks to be removed.
     */
    private final List<GlowTask> oldTasks = new ArrayList<GlowTask>();

    /**
     * A list of active tasks.
     */
    private final List<GlowTask> tasks = new ArrayList<GlowTask>();

    private final List<GlowWorker> activeWorkers = Collections.synchronizedList(new ArrayList<GlowWorker>());

    /**
     * Creates a new task scheduler.
     */
    public GlowScheduler(GlowServer server) {
        this.server = server;

        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    pulse();
                } catch (Exception ex) {
                    GlowServer.logger.log(Level.SEVERE, "Error while pulsing", ex);
                }
            }
        }, 0, PULSE_EVERY, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Stops the scheduler and all tasks.
     */
    public void stop() {
        cancelAllTasks();
        executor.shutdown();
    }

    /**
     * Schedules the specified task.
     * @param task The task.
     */
    private GlowTask schedule(GlowTask task) {
        synchronized (newTasks) {
            newTasks.add(task);
        }
        return task;
    }

    /**
     * Adds new tasks and updates existing tasks, removing them if necessary.
     */
    private void pulse() {
        // Perform basic world pulse.
        server.getSessionRegistry().pulse();
        for (World world : server.getWorlds())
            ((GlowWorld) world).pulse();
        
        // Bring in new tasks this tick.
        synchronized (newTasks) {
            for (GlowTask task : newTasks) {
                tasks.add(task);
            }
            newTasks.clear();
        }
        
        // Remove old tasks this tick.
        synchronized (oldTasks) {
            for (GlowTask task : oldTasks) {
                tasks.remove(task);
            }
            oldTasks.clear();
        }

        // Run the relevant tasks.
        for (Iterator<GlowTask> it = tasks.iterator(); it.hasNext(); ) {
            GlowTask task = it.next();
            boolean cont = false;
            try {
                if (task.isSync()) {
                    cont = task.pulse();
                } else {
                    activeWorkers.add(new GlowWorker(task, this));
                }
            } finally {
                if (!cont) it.remove();
            }
        }
    }

    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return scheduleSyncRepeatingTask(plugin, task, delay, -1);
    }

    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleSyncDelayedTask(plugin, task, 0);
    }

    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, true, delay, period)).getTaskId();
    }

    @Deprecated
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return scheduleAsyncRepeatingTask(plugin, task, delay, -1);
    }

    @Deprecated
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleAsyncRepeatingTask(plugin, task, 0, -1);
    }

    @Deprecated
    public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, false, delay, period)).getTaskId();
    }

    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BukkitTask runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return runTaskLater(plugin, task, 0);
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return runTaskLaterAsynchronously(plugin, task, 0);
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return runTaskTimer(plugin, task, delay, -1);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return runTaskTimerAsynchronously(plugin, task, delay, -1);
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return schedule(new GlowTask(plugin, task, true, delay, period));
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return schedule(new GlowTask(plugin, task, false, delay, period));
    }

    public void cancelTask(int taskId) {
        synchronized(oldTasks) {
            for (GlowTask task : tasks) {
                if (task.getTaskId() == taskId) {
                    oldTasks.add(task);
                    return;
                }
            }
        }
    }

    public void cancelTasks(Plugin plugin) {
        synchronized(oldTasks) {
            for (GlowTask task : tasks) {
                if (task.getOwner() == plugin) {
                    oldTasks.add(task);
                }
            }
        }
    }

    public void cancelAllTasks() {
        synchronized(oldTasks) {
            oldTasks.addAll(tasks);
        }
    }

    public boolean isCurrentlyRunning(int taskId) {
        for (GlowWorker worker : activeWorkers) {
            if (worker.getTaskId() == taskId && worker.getThread().isAlive()) return true;
        }
        return false;
    }

    public boolean isQueued(int taskId) {
        synchronized (tasks) {
            for (GlowTask task : tasks) {
                if (task.getTaskId() == taskId) return true;
            }
        }
        return false;
    }

    public List<BukkitWorker> getActiveWorkers() {
        return new ArrayList<BukkitWorker>(activeWorkers);
    }

    public List<BukkitTask> getPendingTasks() {
        return new ArrayList<BukkitTask>(tasks);
    }

    synchronized void workerComplete(GlowWorker worker) {
        activeWorkers.remove(worker);
        if (!worker.shouldContinue()) {
            oldTasks.add(worker.getTask());
        }
    }

}
