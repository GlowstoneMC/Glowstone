package net.glowstone.scheduler;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.net.SessionRegistry;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

/**
 * A scheduler for managing server ticks, Bukkit tasks, and other synchronization.
 */
public final class GlowScheduler implements BukkitScheduler {

    /**
     * The number of milliseconds between pulses.
     */
    static final int PULSE_EVERY = 50;
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    /**
     * The server this scheduler is managing for.
     */
    private final Server server;
    /**
     * The scheduled executor service which backs this worlds.
     */
    private final ScheduledExecutorService executor = Executors
        .newSingleThreadScheduledExecutor(GlowThreadFactory.INSTANCE);
    /**
     * Executor to handle execution of async tasks.
     */
    private final ExecutorService asyncTaskExecutor
            = new ThreadPoolExecutor(0, MAX_THREADS, 60L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(), GlowThreadFactory.INSTANCE);
    /**
     * A list of active tasks.
     */
    private final ConcurrentMap<Integer, GlowTask> tasks = new ConcurrentHashMap<>();
    /**
     * World tick scheduler.
     */
    private final WorldScheduler worlds;
    /**
     * Tasks to be executed during the tick.
     */
    private final Deque<Runnable> inTickTasks = new ConcurrentLinkedDeque<>();
    /**
     * Condition to wait on when processing in-tick tasks.
     */
    private final Object inTickTaskCondition;
    /**
     * Runnable to run at end of tick.
     */
    private final Runnable tickEndRun;
    /**
     * The primary worlds thread in which pulse() is called.
     */
    private Thread primaryThread;
    /**
     * The session registry used to pulse all players.
     */
    private final SessionRegistry sessionRegistry;

    /**
     * Creates a new task scheduler.
     *
     * @param server The server that will use this scheduler.
     * @param worlds The {@link WorldScheduler} this scheduler will use for ticking the server's
     *         worlds.
     */
    public GlowScheduler(GlowServer server, WorldScheduler worlds) {
        this(server, worlds, server.getSessionRegistry());
    }

    /**
     * Creates a new task scheduler.
     * @param server The server that will use this scheduler.
     * @param worlds The {@link WorldScheduler} this scheduler will use for ticking the server's
     *         worlds.
     * @param sessionRegistry The {@link SessionRegistry} this scheduler will use to tick players
     */
    public GlowScheduler(Server server, WorldScheduler worlds,
            SessionRegistry sessionRegistry) {
        this.server = server;
        this.worlds = worlds;
        this.sessionRegistry = sessionRegistry;
        inTickTaskCondition = worlds.getAdvanceCondition();
        tickEndRun = this.worlds::doTickEnd;
        primaryThread = Thread.currentThread();
    }

    /**
     * Starts running ticks.
     */
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            try {
                pulse();
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error while pulsing", ex);
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
            inTickTasks.stream().filter(task -> task instanceof Future)
                .forEach(task -> ((Future) task).cancel(false));
            inTickTasks.clear();
        }
    }

    /**
     * Schedules the specified task.
     *
     * @param task The task.
     */
    private GlowTask schedule(GlowTask task) {
        tasks.put(task.getTaskId(), task);
        return task;
    }

    /**
     * Checks if the current {@link Thread} is the server's primary thread.
     *
     * @return If the current {@link Thread} is the server's primary thread.
     */
    public boolean isPrimaryThread() {
        return Thread.currentThread() == primaryThread;
    }

    /**
     * Schedules the given task for the start of the next tick.
     *
     * @param run the task to run
     */
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
     */
    // TODO: Add watchdog system to make sure ticks advance
    private void pulse() {
        primaryThread = Thread.currentThread();

        // Process player packets
        sessionRegistry.pulse();

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
                    break;
                default:
                    // do nothing
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
        } finally {
            System.out.flush();
            System.err.flush();
        }

    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return scheduleSyncDelayedTask(plugin, task, 0);
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return scheduleSyncRepeatingTask(plugin, task, delay, -1);
    }

    @Override
    @Deprecated
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task) {
        return task.runTask(plugin).getTaskId();
    }

    @Override
    @Deprecated
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable task, long delay) {
        return task.runTaskLater(plugin, delay).getTaskId();
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return schedule(new GlowTask(plugin, task, true, delay, period)).getTaskId();
    }

    @Override
    @Deprecated
    public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable task, long delay,
        long period) {
        return task.runTaskTimer(plugin, delay, period).getTaskId();
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
    public BukkitTask runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return runTaskLater(plugin, task, 0);
    }

    @Override
    @Deprecated
    public BukkitTask runTask(Plugin plugin, BukkitRunnable task) throws IllegalArgumentException {
        return task.runTask(plugin);
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable task)
        throws IllegalArgumentException {
        return runTaskLaterAsynchronously(plugin, task, 0);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable task)
        throws IllegalArgumentException {
        return task.runTaskAsynchronously(plugin);
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, Runnable task, long delay)
        throws IllegalArgumentException {
        return runTaskTimer(plugin, task, delay, -1);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable task, long delay)
        throws IllegalArgumentException {
        return task.runTaskLater(plugin, delay);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay)
        throws IllegalArgumentException {
        return runTaskTimerAsynchronously(plugin, task, delay, -1);
    }

    @Override
    @Deprecated
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable task, long delay)
        throws IllegalArgumentException {
        return task.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, Runnable task, long delay, long period)
        throws IllegalArgumentException {
        return schedule(new GlowTask(plugin, task, true, delay, period));
    }

    @Override
    @Deprecated
    public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable task, long delay, long period)
        throws IllegalArgumentException {
        return task.runTaskTimer(plugin, delay, period);
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay,
        long period) throws IllegalArgumentException {
        return schedule(new GlowTask(plugin, task, false, delay, period));
    }

    @Override
    @Deprecated
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable task, long delay,
        long period) throws IllegalArgumentException {
        return task.runTaskTimerAsynchronously(plugin, delay, period);
    }

    @Override
    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        runTask(plugin, future);
        return future;
    }

    /**
     * Runs a task on the primary thread, and blocks waiting for it to finish.
     *
     * @param task the task to run
     * @param <T> the task's return type
     * @return the task result
     * @throws Exception if thrown by the task
     */
    public <T> T syncIfNeeded(Callable<T> task) throws Exception {
        if (isPrimaryThread()) {
            return task.call();
        } else {
            return callSyncMethod(null, task).get();
        }
    }

    @Override
    public void cancelTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        tasks.values().removeIf(glowTask -> glowTask.getOwner() == plugin);
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
     * Returns active async tasks.
     *
     * @return active async tasks
     */
    @Override
    public List<BukkitWorker> getActiveWorkers() {
        return ImmutableList.copyOf(Collections2.filter(tasks.values(),
            glowTask -> glowTask != null && !glowTask.isSync()
                && glowTask.getLastExecutionState() == TaskExecutionState.RUN));
    }

    /**
     * Returns tasks that still have at least one run remaining.
     *
     * @return the tasks to be run
     */
    @Override
    public List<BukkitTask> getPendingTasks() {
        return new ArrayList<>(tasks.values());
    }

    private static class GlowThreadFactory implements ThreadFactory {

        public static final GlowThreadFactory INSTANCE = new GlowThreadFactory();
        private final AtomicInteger threadCounter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Glowstone-scheduler-" + threadCounter.getAndIncrement());
        }
    }
}
