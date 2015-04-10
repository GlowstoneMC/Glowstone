package net.glowstone.world;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.WorldCreator;

public class LoadWorld extends Thread {

    private final GlowServer server;
    private final WorldCreator creator;
    private GlowWorld world;

    public LoadWorld(GlowServer server, WorldCreator creator) {
        this.server = server;
        this.creator = creator;
    }

    @Override
    public void run() {
        this.world = server.getWorld(creator.name());
        if (world != null) {
            return;
        }
        if (creator.generator() == null) {
            creator.generator(server.getGenerator(creator.name(), creator.environment(), creator.type()));
        }

        this.world = new GlowWorld(server, creator);
    }
    
    public GlowWorld createWorld() {
        start();
        return world;
    }
    
}
