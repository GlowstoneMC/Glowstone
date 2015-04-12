package net.glowstone.world;

import net.glowstone.GlowChunk;

public class PopulateChunk extends Thread {
    
    private final GlowChunk chunk;
    
    public PopulateChunk(GlowChunk chunk) {
        this.chunk = chunk;
    }
    
    @Override
    public void run() {
        chunk.getWorld().getChunkManager().populateChunk(chunk.getX(), chunk.getZ(), false);
    }
}

