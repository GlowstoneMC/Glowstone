package net.glowstone.util;

import net.glowstone.GlowServer;

import java.util.Map;

/**
 * Thread started on shutdown that monitors for and kills rogue non-daemon threads.
 */
public class ShutdownMonitorThread extends Thread {

    /**
     * The delay in milliseconds until leftover threads are killed.
     */
    private static final int DELAY = 8000;

    public ShutdownMonitorThread() {
        setName("ShutdownMonitorThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            GlowServer.logger.severe("ShutdownMonitor interrupted");
            return;
        }

        GlowServer.logger.warning("Still running after shutdown, finding rouge threads...");

        final Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
            final Thread thread = entry.getKey();
            final StackTraceElement[] stack = entry.getValue();

            if (thread.isDaemon() || !thread.isAlive() || stack.length == 0) {
                // won't keep JVM from exiting
                continue;
            }

            GlowServer.logger.warning("Rogue thread: " + thread);
            for (StackTraceElement trace : stack) {
                GlowServer.logger.warning("\tat " + trace);
            }

            // really get it out of there
            thread.interrupt();
            thread.stop();
        }
    }

}
