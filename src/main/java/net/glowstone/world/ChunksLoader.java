package net.glowstone.world;

import java.util.ArrayList;
import net.glowstone.GlowWorld;

public class ChunksLoader extends Thread {
    
    private ArrayList<GlowWorld> worlds;
    private ArrayList<int[]> chunks;
    private int chunkCount;
    
    public ChunksLoader() {
        worlds = new ArrayList<>();
        chunks = new ArrayList<>();
        chunkCount = 0;
    }
    
    @Override
    public void run() {
        for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
            worlds.get(chunkIndex).loadChunk(chunks.get(chunkIndex)[0], chunks.get(chunkIndex)[1], true);
        }
    }
    
    public void addChunk(GlowWorld world, int x, int z) {
        worlds.add(world);
        chunks.add(new int[]{x, z});
        chunkCount++;
        if (!isAlive()) {
            start();
        }
    }
}
