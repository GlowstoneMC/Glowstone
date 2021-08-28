package net.glowstone.scheduler;

import lombok.Getter;
import net.glowstone.GlowServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a task which is executed periodically.
 *
 * @author Graham Edgecombe
 */
public class GlowTask extends FutureTask<Void> implements BukkitTask, BukkitWorker {

    /**
     * The next task ID pending.
     */
    private static final AtomicInteger nextTaskId = new AtomicInteger(0);

    /**
     * The ID of this task.
     */
    @Getter
    private final int taskId;

    /**
     * The Plugin that owns this task.
     */
    @Getter
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
     * A flag indicating whether this task is to be run asynchronously.
     */
    @Getter
    private final boolean sync;
    /**
     * A description of the runnable assigned to this task.
     */
    private final String description;
    /**
     * The current number of ticks since last initialization.
     */
    private long counter;
    /**
     * The thread this task has been last executed on, if this task is async.
     */
    @Getter
    private Thread thread;
    /**
     * The last execution state returned by {@link #shouldExecute()} (most likely the state the task
     * is currently in).
     */
    @Getter
    private volatile TaskExecutionState lastExecutionState = TaskExecutionState.WAIT;

    /**
     * Creates a new task with the specified number of ticks between consecutive calls to
     * execute().
     *
     * @param owner The plugin which started the task.
     * @param task The runnable for this task.
     * @param sync If the task should be run synchronously.
     * @param delay The delay in ticks before running this task.
     * @param period The delay in ticks before running this task again. -1 for no repeat.
     */
    public GlowTask(Plugin owner, Runnable task, boolean sync, long delay, long period) {
        super(task, null);
        taskId = nextTaskId.getAndIncrement();
        description = task.toString();
        this.owner = owner;
        this.delay = delay;
        this.period = period;
        counter = 0;
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "GlowTask{"
                + "id=" + taskId
                + ", plugin=" + owner
                + ", sync=" + sync
                + ": " + description
                + '}';
    }

    /**
     * Stops this task.
     */
    @Override
    public void cancel() {
        cancel(false);
    }

    /**
     * Called every 'pulse' which is around 50ms in Minecraft.
     *
     * <p>This method updates the counters and returns whether execute() should be called
     *
     * @return Execution state for this task
     */
    TaskExecutionState shouldExecute() {
        TaskExecutionState execState = shouldExecuteUpdate();
        lastExecutionState = execState;
        return execState;
    }

    private TaskExecutionState shouldExecuteUpdate() {
        // Stop running if cancelled, exception, or not repeating
        if (isDone()) {
            return TaskExecutionState.STOP;
        }

        ++counter;
        if (counter >= delay) {
            if (period == -1 || (counter - delay) % period == 0) {
                return TaskExecutionState.RUN;
            }
        }

        return TaskExecutionState.WAIT;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        if (period == -1) {
            super.run();
        } else {
            runAndReset();
        }
    }

    @Override
    protected void done() {
        super.done();
        if (isCancelled()) {
            return;
        }

        try {
            get();
        } catch (ExecutionException ex) {
            Logger log = owner == null ? GlowServer.logger : owner.getLogger();
            log.log(Level.SEVERE, "Error while executing " + this, ex.getCause());
        } catch (InterruptedException e) {
            // Task is already done, see the fact that we're in done() method
        }
    }
}
