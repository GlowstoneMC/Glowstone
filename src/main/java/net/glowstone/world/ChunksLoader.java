package net.glowstone.world;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import net.glowstone.GlowChunk;
import static net.glowstone.GlowServer.logger;

public class ChunksLoader extends Thread {

    private ArrayList<GlowChunk> chunks;
    private int chunkCount;
    public CountDownLatch worldLoad = new CountDownLatch(3);
    
    public ChunksLoader() {
        chunks = new ArrayList<>();
        chunkCount = 0;
    }
    
    @Override
    public void run() {
        try {
            worldLoad.await();
            logger.info("Loading chunks: 0%");
            long loadTime = System.currentTimeMillis();
            for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
                GlowChunk chunk = chunks.get(chunkIndex);
                if (!chunk.isLoaded()) {
                    new LoadChunk(chunk).start();
                }
                if (System.currentTimeMillis() >= loadTime + 250) {
                    int progress = 100 * chunkIndex / chunkCount;
                    logger.info("Loading chunks: " + progress + "%");
                    loadTime = System.currentTimeMillis();
                }
            }
            logger.info("Preparing chunks: 0%");
            loadTime = System.currentTimeMillis();
            for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
                GlowChunk chunk = chunks.get(chunkIndex);
                if (!chunk.isPopulated()) {
                    new PopulateChunk(chunk).start();
                }
                if (System.currentTimeMillis() >= loadTime + 250) {
                    int progress = 100 * chunkIndex / chunkCount;
                    logger.info("Preparing chunks: " + progress + "%");
                    loadTime = System.currentTimeMillis();
                }
            }
            logger.info("Loading chunks: done");
        } catch (InterruptedException e) {
            logger.severe("Chunk loader interrupted: " + e);
        }
    }

    public void addChunk(GlowChunk chunk) {
        chunks.add(chunk);
        chunkCount++;
    }
}
