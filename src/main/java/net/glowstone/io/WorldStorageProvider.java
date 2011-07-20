package net.glowstone.io;

import net.glowstone.GlowWorld;

public interface WorldStorageProvider {

    public ChunkIoService getChunkIoService();

    public WorldMetadataService getMetadataService();

    public void setWorld(GlowWorld world);

}
