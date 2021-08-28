package net.glowstone.util;

import net.glowstone.GlowServer;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

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
            GlowServer.logger.log(Level.SEVERE, "Shutdown monitor interrupted", e);
            System.exit(0);
            return;
        }

        GlowServer.logger.info("Still running after shutdown, finding rogue threads...");

        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stack = entry.getValue();

            if (thread.isDaemon() || !thread.isAlive() || stack.length == 0) {
                // won't keep JVM from exiting
                continue;
            }

            // ask nicely to kill them
            thread.interrupt();
            // wait for them to die on their own
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                GlowServer.logger.log(Level.SEVERE, "Shutdown monitor interrupted", e);
                System.exit(0);
                return;
            }
        }
        // kill them forcefully
        GlowServer.logger.info("Rogue threads killed, shutting down.");
        System.exit(0);
    }

}
