package net.glowstone.scheduler;

import com.google.common.collect.ImmutableList;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Phaser;
import java.util.logging.Level;

/**
 * Manager for world thread pool.
 * <p/>
 * This is a little magical and finnicky, so tread with caution when messing with the phasers
 */
public class WorldScheduler {
    private static class WorldEntry {
        private final GlowWorld world;
        private WorldThread task;

        private WorldEntry(GlowWorld world) {
            this.world = world;
        }
    }

    private final Object advanceCondition = new Object();
    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);
    private final List<WorldEntry> worlds = new CopyOnWriteArrayList<>();
    private volatile int currentTick = -1;

    private class WorldThread extends Thread {
        private final GlowWorld world;

        public WorldThread(GlowWorld world) {
            super("Glowstone-world-" + world.getName());
            this.world = world;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted() && !tickEnd.isTerminated()) {
                    tickBegin.arriveAndAwaitAdvance();
                    try {
                        world.pulse();
                    } catch (Exception e) {
                        GlowServer.logger.log(Level.SEVERE, "Error occurred while pulsing world " + world.getName(), e);
                    } finally {
                        tickEnd.arriveAndAwaitAdvance();
                    }
                }
            } finally {
                tickBegin.arriveAndDeregister();
                tickEnd.arriveAndDeregister();
            }
        }
    }

    public List<GlowWorld> getWorlds() {
        ImmutableList.Builder<GlowWorld> ret = ImmutableList.builder();
        for (WorldEntry entry : worlds) {
            ret.add(entry.world);
        }
        return ret.build();
    }

    public GlowWorld getWorld(String name) {
        for (WorldEntry went : worlds) {
            if (went.world.getName().equals(name)) {
                return went.world;
            }
        }
        return null;
    }

    public GlowWorld getWorld(UUID uid) {
        for (WorldEntry went : worlds) {
            if (went.world.getUID().equals(uid)) {
                return went.world;
            }
        }
        return null;
    }

    public GlowWorld addWorld(final GlowWorld world) {
        final WorldEntry went = new WorldEntry(world);
        worlds.add(went);
        try {
            went.task = new WorldThread(world);
            tickBegin.register();
            tickEnd.register();
            went.task.start();
            return world;
        } catch (Throwable t) {
            tickBegin.arriveAndDeregister();
            tickEnd.arriveAndDeregister();
            worlds.remove(went);
            return null;
        }
    }

    public boolean removeWorld(final GlowWorld world) {
        for (WorldEntry entry : worlds) {
            if (entry.world.equals(world)) {
                if (entry.task != null) {
                    entry.task.interrupt();
                }
                worlds.remove(entry);
                return true;
            }
        }
        return false;
    }

    int beginTick() throws InterruptedException {
        tickEnd.awaitAdvanceInterruptibly(currentTick); // Make sure previous tick is complete
        return currentTick = tickBegin.arrive();
    }

    boolean isTickComplete(int tick) {
        return tickEnd.getPhase() > tick || tickEnd.getPhase() < 0;
    }

    void stop() {
        tickBegin.forceTermination();
        tickEnd.forceTermination();
        for (WorldEntry ent : worlds) {
            if (ent.task != null) {
                ent.task.interrupt();
            }
        }
    }

    void doTickEnd() {
        final int currentTick = this.currentTick;
        // Mark ourselves as arrived so world threads automatically trigger advance once done
        int endPhase = tickEnd.arriveAndAwaitAdvance();
        if (endPhase != currentTick + 1) {
            GlowServer.logger.warning("Tick end barrier " + endPhase + " has advanced differently from tick begin barrier:" + currentTick + 1);
        }
        synchronized (advanceCondition) {
            advanceCondition.notifyAll();
        }
    }

    public Object getAdvanceCondition() {
        return advanceCondition;
    }
}
