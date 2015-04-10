package net.glowstone.world;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import net.glowstone.GlowChunk;
import static net.glowstone.GlowServer.logger;

public class ChunksLoader extends Thread {

    private ArrayList<GlowChunk> chunks;
    private int chunkCount;
    public CountDownLatch worldLoad = new CountDownLatch(4);
    
    public ChunksLoader() {
        chunks = new ArrayList<>();
        chunkCount = 0;
    }
    
    @Override
    public void run() {
        long loadTime = System.currentTimeMillis();
        for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
            new LoadChunk(chunks.get(chunkIndex)).start();
            if (System.currentTimeMillis() >= loadTime + 1000) {
                int progress = 100 * chunkIndex / chunkCount;
                logger.info("Loading chunks: " + progress + "%");
                loadTime = System.currentTimeMillis();
            }
        }
        logger.info("Loading chunks: done");
    }

    public void addChunk(GlowChunk chunk) {
        chunks.add(chunk);
        chunkCount++;
        if (chunkCount > 1000 && !isAlive()) {
            start();
        }
    }
}
