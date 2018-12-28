package net.glowstone.diag994;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused") // intermittently needed for debugging
public class DeadlockWatchdogThread extends LooperThread {

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
    private static final Logger LOG = LoggerFactory.getLogger(DeadlockWatchdogThread.class);
    private static final int MAX_STACK_DEPTH = 20;
    private static final int DEADLOCK_STATUS = 0xDEAD10CC;
    private static DeadlockWatchdogThread INSTANCE = new DeadlockWatchdogThread();

    private static final class StackTraceHolder extends Throwable {
        public StackTraceHolder(final String name, final StackTraceElement[] stackTrace) {
            super(name, null, false, true);
            setStackTrace(stackTrace);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            // No-op: we only use the stack trace that's in our constructor parameter
            return this;
        }
    }

    private DeadlockWatchdogThread() {
        super("net.glowstone.diag994.DeadlockWatchdogThread");
    }

    public static void ensureStarted() {
        synchronized (DeadlockWatchdogThread.class) {
            if (INSTANCE.getState() == Thread.State.TERMINATED) {
                INSTANCE = new DeadlockWatchdogThread();
            }
            if (INSTANCE.getState() == Thread.State.NEW) {
                INSTANCE.setDaemon(true);
                INSTANCE.setPriority(Thread.MAX_PRIORITY);
                INSTANCE.start();
            }
        }
    }

    public static void stopInstance() {
        synchronized (DeadlockWatchdogThread.class) {
            INSTANCE.interrupt();
            INSTANCE = new DeadlockWatchdogThread();
        }
    }

    @SuppressWarnings({"CallToSystemExit", "ConstantConditions", "ObjectAllocationInLoop"})
    @Override
    public boolean iterate()
            throws InterruptedException {
        sleep(60_000);
        boolean deadlockFound = false;
        long[] threadsOfInterest = THREAD_MX_BEAN.findDeadlockedThreads();
        if ((threadsOfInterest != null) && (threadsOfInterest.length > 0)) {
            LOG.error("DEADLOCKED THREADS FOUND");
            deadlockFound = true;
        } else {
            threadsOfInterest = THREAD_MX_BEAN.getAllThreadIds();
            if (threadsOfInterest.length <= 0) {
                LOG.error("ThreadMxBean didn't return any thread IDs");
                return false;
            }
        }
        for (final long id : threadsOfInterest) {
            final ThreadInfo threadInfo = THREAD_MX_BEAN.getThreadInfo(id, MAX_STACK_DEPTH);
            final StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            final Throwable t = new StackTraceHolder(threadInfo.getThreadName(), stackTrace);
            if (deadlockFound) {
                LOG.error("A deadlocked thread:", t);
            } else {
                LOG.info("A running thread:", t);
            }
        }
        if (deadlockFound) {
            // Fail fast if current context allows
            System.exit(DEADLOCK_STATUS);
        }
        return !deadlockFound; // Terminate when a deadlock is found
    }
}
