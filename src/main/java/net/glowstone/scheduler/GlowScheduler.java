package net.glowstone.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.World;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

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

    /**
     * Creates a new task scheduler.
     */
    public GlowScheduler(GlowServer server) {
        this.server = server;
        
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    pulse();
                }
                catch (Exception ex) {
                    GlowServer.logger.log(Level.SEVERE, "Error while pulsing: {0}", ex.getMessage());
                    ex.printStackTrace();
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
    private int schedule(GlowTask task) {
        synchronized (newTasks) {
            newTasks.add(task);
        }
        return task.getTaskId();
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
                cont = task.pulse();
            } finally {
                if (!cont) it.remove();
            }
        }
    }

    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return schedule(new GlowTask(plugin, task, delay, -1));
    }

    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return schedule(new GlowTask(plugin, task, 0, -1));
    }

    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, delay, period));
    }

    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        GlowServer.logger.log(Level.WARNING, "Plugin {0}: Async tasks are not yet supported", plugin.getDescription().getName());
        return scheduleSyncDelayedTask(plugin, task, delay);
    }

    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
        GlowServer.logger.log(Level.WARNING, "Plugin {0}: Async tasks are not yet supported", plugin.getDescription().getName());
        return scheduleSyncDelayedTask(plugin, task, 0);
    }

    public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        GlowServer.logger.log(Level.WARNING, "Plugin {0}: Async tasks are not yet supported", plugin.getDescription().getName());
        return scheduleSyncRepeatingTask(plugin, task, delay, period);
    }

    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancelTask(int taskId) {
        synchronized(oldTasks) {
            for (Iterator<GlowTask> it = tasks.iterator(); it.hasNext(); ) {
                GlowTask task = it.next();
                if (task.getTaskId() == taskId) {
                    oldTasks.add(task);
                    return;
                }
            }
        }
    }

    public void cancelTasks(Plugin plugin) {
        synchronized(oldTasks) {
            for (Iterator<GlowTask> it = tasks.iterator(); it.hasNext(); ) {
                GlowTask task = it.next();
                if (task.getOwner() == plugin) {
                    oldTasks.add(task);
                }
            }
        }
    }

    public void cancelAllTasks() {
        synchronized(oldTasks) {
            for (Iterator<GlowTask> it = tasks.iterator(); it.hasNext(); ) {
                oldTasks.add(it.next());
            }
        }
    }

    public boolean isCurrentlyRunning(int taskId) {
        // Can safely return false since this only refers to async tasks.
        return false;
    }

    public boolean isQueued(int taskId) {
        // Can safely return false since this only refers to async tasks.
        return false;
    }

    public List<BukkitWorker> getActiveWorkers() {
        return new ArrayList<BukkitWorker>();
    }

    public List<BukkitTask> getPendingTasks() {
        ArrayList<BukkitTask> result = new ArrayList<BukkitTask>();
        for (Iterator<GlowTask> it = tasks.iterator(); it.hasNext(); ) {
            result.add(it.next());
        }
        return result;
    }

}
