package net.glowstone.world;

import net.glowstone.GlowServer;
import static net.glowstone.GlowServer.logger;

public class SpawnLoader implements Runnable {

    private GlowServer server;
    
    public SpawnLoader(GlowServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        long loadTime = System.currentTimeMillis();
        int chunkIndex = 0;
        for (chunkIndex = 0; chunkIndex < server.spawnChunkCount; chunkIndex++) {
            server.spawnWorlds.get(chunkIndex).loadChunk(server.spawnChunks.get(chunkIndex)[0], server.spawnChunks.get(chunkIndex)[1], true);
            chunkIndex++;
            if (System.currentTimeMillis() >= loadTime + 1000) {
                int progress = 100 * chunkIndex / server.spawnChunks.size();
                logger.info("Loading spawns: " + progress + "%");
                loadTime = System.currentTimeMillis();
            } 
        }
    }

    
}
