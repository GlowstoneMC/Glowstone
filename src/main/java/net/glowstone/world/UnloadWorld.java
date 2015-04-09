package net.glowstone.world;

import net.glowstone.GlowServer;
import static net.glowstone.GlowServer.logger;
import net.glowstone.GlowWorld;
import org.bukkit.World;

public class UnloadWorld implements Runnable {
    
    private final GlowServer server;
    private final World bWorld;
    private final boolean save;
    private boolean unloaded;
    
    public UnloadWorld(GlowServer server, World bWorld, boolean save) {
        this.server = server;
        this.bWorld = bWorld;
        this.save = save;
    }
    
    @Override
    public void run() {
        if (!(bWorld instanceof GlowWorld)) {
            unloaded = false;
        }
        GlowWorld world = (GlowWorld) bWorld;
        if (save) {
            world.setAutoSave(false);
            world.save(false);
        }
        if (server.worlds.removeWorld(world)) {
            world.unload();
            unloaded = true;
        }
        unloaded = false;
        logger.info("Saved world: " + world.getName());
    }
    
    public boolean unloadWorld() {
        run();
        return unloaded;
    }
}
