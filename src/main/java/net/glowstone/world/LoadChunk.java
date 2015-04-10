package net.glowstone.world;

import net.glowstone.GlowWorld;

public class LoadChunk extends Thread {
    
    private final GlowWorld world;
    private final int x;
    private final int z;
    
    public LoadChunk(GlowWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }
    
    @Override
    public void run() {
        world.getChunkManager().loadChunk(x, z, true);
        if (!world.isChunkLoaded(x, z)) {
            world.getServer().chunksLoader.addChunk(world, x, z);
        }
    }
    
}
