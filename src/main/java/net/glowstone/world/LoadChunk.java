package net.glowstone.world;

import net.glowstone.GlowChunk;

public class LoadChunk extends Thread {
    
    private final GlowChunk chunk;
    
    public LoadChunk(GlowChunk chunk) {
        this.chunk = chunk;
    }
    
    @Override
    public void run() {
        chunk.load();
        chunk.getWorld().getChunkManager().forcePopulation(chunk.getX(), chunk.getZ());
        chunk.getWorld().spawnChunkLock.acquire(new GlowChunk.Key(chunk.getX(), chunk.getZ()));
    }
}
