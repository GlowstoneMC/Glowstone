package net.glowstone.io;

import java.io.File;
import net.glowstone.GlowWorld;

public interface WorldStorageProvider {

    public ChunkIoService getChunkIoService();

    public WorldMetadataService getMetadataService();

    public void setWorld(GlowWorld world);

    /** Get the folder holding the world data.
     * @return world folder
     */
    public File getFolder();

}
