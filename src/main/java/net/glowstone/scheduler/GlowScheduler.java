package net.glowstone.scheduler;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import net.glowstone.GlowServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * A scheduler for managing server ticks, Bukkit tasks, and other synchronization.
 */
public final class GlowScheduler implements BukkitScheduler {

    private static class GlowThreadFactory implements ThreadFactory {
        public static final GlowThreadFactory INSTANCE = new GlowThreadFactory();
        private final AtomicInteger threadCounter = new AtomicInteger();

        private GlowThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Glowstone-scheduler-" + threadCounter.getAndIncrement());
        }
    }

    /**
     * The number of milliseconds between pulses.
     */
    static final int PULSE_EVERY = 50;

    /**
     * The server this scheduler is managing for.
     */
    private final GlowServer server;

    /**
     * The scheduled executor service which backs this worlds.
     */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(GlowThreadFactory.INSTANCE);

    /**
     * Executor to handle execution of async tasks
     */
    private final ExecutorService asyncTaskExecutor = Executors.newCachedThreadPool(GlowThreadFactory.INSTANCE);

    /**
     * A list of active tasks.
     */
    private final ConcurrentMap<Integer, GlowTask> tasks = new ConcurrentHashMap<>();

    /**
     * The primary worlds thread in which pulse() is called.
     */
    private Thread primaryThread;

    /**
     * World tick scheduler
     */
    private final WorldScheduler worlds;

    /**
     * Tasks to be executed during the tick
     */
    private final Deque<Runnable> inTickTasks = new ConcurrentLinkedDeque<>();

    /**
     * Condition to wait on when processing in tick tasks
     */
    private final Object inTickTaskCondition;

    /**
     * Runnable to run at end of tick
     */
    private final Runnable tickEndRun;

    /**
     * Creates a new task scheduler.
     */
    public GlowScheduler(GlowServer server, WorldScheduler worlds) {
        this.server = server;
        this.worlds = worlds;
        inTickTaskCondition = worlds.getAdvanceCondition();
        tickEndRun = new Runnable() {
            @Override
            public void run() {
                GlowScheduler.this.worlds.doTickEnd();
            }
        };
        primaryThread = Thread.currentThread();
    }

    public void start() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
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
        worlds.stop();
        executor.shutdownNow();
        asyncTaskExecutor.shutdown();

        synchronized (inTickTaskCondition) {
            for (Runnable task : inTickTasks) {
                if (task instanceof Future) {
                    ((Future) task).cancel(false);
                }
            }
            inTickTasks.clear();
        }
    }

    /**
     * Schedules the specified task.
     * @param task The task.
     */
    private GlowTask schedule(GlowTask task) {
        tasks.put(task.getTaskId(), task);
        return task;
    }

    /**
     * Returns true if the current {@link Thread} is the server's primary thread.
     */
    public boolean isPrimaryThread() {
        return Thread.currentThread() == primaryThread;
    }

    public void scheduleInTickExecution(Runnable run) {
        if (isPrimaryThread() || executor.isShutdown()) {
            run.run();
        } else {
            synchronized (inTickTaskCondition) {
                inTickTasks.addFirst(run);
                inTickTaskCondition.notifyAll();
            }
        }
    }

    /**
     * Adds new tasks and updates existing tasks, removing them if necessary.
     * <p/>
     * todo: Add watchdog system to make sure ticks advance
     */
    private void pulse() {
        primaryThread = Thread.currentThread();

        // Process player packets
        server.getSessionRegistry().pulse();

        // Run the relevant tasks.
        for (Iterator<GlowTask> it = tasks.values().iterator(); it.hasNext(); ) {
            GlowTask task = it.next();
            switch (task.shouldExecute()) {
                case RUN:
                    if (task.isSync()) {
                        task.run();
                    } else {
                        asyncTaskExecutor.submit(task);
                    }
                    break;
                case STOP:
                    it.remove();
            }
        }
        try {
            int currentTick = worlds.beginTick();
            try {
                asyncTaskExecutor.submit(tickEndRun);
            } catch (RejectedExecutionException ex) {
                worlds.stop();
                return;
            }

            Runnable tickTask;
            synchronized (inTickTaskCondition) {
                while (!worlds.isTickComplete(currentTick)) {
                    while ((tickTask = inTickTasks.poll()) != null) {
                        tickTask.run();
                    }

                    inTickTaskCondition.wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return scheduleSyncRepeatingTask(plugin, task, delay, -1);
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleSyncDelayedTask(plugin, task, 0);
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, true, delay, period)).getTaskId();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return scheduleAsyncRepeatingTask(plugin, task, delay, -1);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleAsyncRepeatingTask(plugin, task, 0, -1);
    }

    @Override
    @Deprecated
    public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, false, delay, period)).getTaskId();
    }

    @Override
    @Deprecated
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task, long delay) {
        return 0;
    }

    @Override
    @Deprecated
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task) {
        return 0;
    }

    @Override
    @Deprecated
    public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable task, long delay, long period) {
        return 0;
    }

    @Override
    @Deprecated
    public BukkitTask runTask(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
        return task.runTask(plugin);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
        return task.runTaskAsynchronously(plugin);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
        return task.runTaskLater(plugin, delay);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable task, long delay) throws IllegalArgumentException {
        return task.runTaskLater(plugin, delay);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        return task.runTaskTimer(plugin, delay, period);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        return task.runTaskTimerAsynchronously(plugin, delay, period);
    }

    @Override
    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        runTask(plugin, future);
        return future;
    }

    public <T> T syncIfNeeded(Callable<T> task) throws Exception {
        if (isPrimaryThread()) {
            return task.call();
        } else {
            return callSyncMethod(null, task).get();
        }
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

    @Override
    public void cancelTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        for (Iterator<GlowTask> it = tasks.values().iterator(); it.hasNext(); ) {
            if (it.next().getOwner() == plugin) {
                it.remove();
            }
        }
    }

    @Override
    public void cancelAllTasks() {
        tasks.clear();
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        GlowTask task = tasks.get(taskId);
        return task != null && task.getLastExecutionState() == TaskExecutionState.RUN;
    }

    @Override
    public boolean isQueued(int taskId) {
        return tasks.containsKey(taskId);
    }

    /**
     * Returns active async tasks
     * @return active async tasks
     */
    @Override
    public List<BukkitWorker> getActiveWorkers() {
        return ImmutableList.<BukkitWorker>copyOf(Collections2.filter(tasks.values(), new Predicate<GlowTask>() {
            @Override
            public boolean apply(@Nullable GlowTask glowTask) {
                return glowTask != null && !glowTask.isSync() && glowTask.getLastExecutionState() == TaskExecutionState.RUN;
            }
        }));
    }

    /**
     * Returns tasks that still have at least one run remaining
     * @return the tasks to be run
     */
    @Override
    public List<BukkitTask> getPendingTasks() {
        return new ArrayList<BukkitTask>(tasks.values());
    }
}
