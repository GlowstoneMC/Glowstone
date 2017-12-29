package net.glowstone;

import java.util.logging.Level;

public class GlowMain implements Runnable {
    private final String[] args;

    public GlowMain(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        try {
            GlowServer server = GlowServer.createFromArguments(args);

            // we don't want to run a server when called with --version
            if (server == null) {
                return;
            }

            server.run();
        } catch (SecurityException e) {
            GlowServer.logger.log(Level.WARNING, "Error loading classpath!", e);
        } catch (Throwable t) {
            // general server startup crash
            GlowServer.logger.log(Level.SEVERE, "Error during server startup.", t);
            System.exit(1);
        }
    }
}
