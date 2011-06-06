package net.glowstone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.glowstone.io.ChunkIoService;
import net.glowstone.world.WorldGenerator;

/**
 * A class which manages the {@link GlowChunk}s currently loaded in memory.
 * @author Graham Edgecombe
 */
public final class ChunkManager {

    /**
     * The chunk I/O service used to read chunks from the disk and write them to
     * the disk.
     */
	private final ChunkIoService service;

    /**
     * The world generator used to generate new chunks.
     */
	private final WorldGenerator generator;
    
    /**
     * The world this ChunkManager is managing.
     */
    private final GlowWorld world;

    /**
     * A map of chunks currently loaded in memory.
     */
	private final Map<GlowChunk.Key, GlowChunk> chunks = new HashMap<GlowChunk.Key, GlowChunk>();

    /**
     * Creates a new chunk manager with the specified I/O service and world
     * generator.
     * @param service The I/O service.
     * @param generator The world generator.
     */
	public ChunkManager(GlowWorld world, ChunkIoService service, WorldGenerator generator) {
        this.world = world;
		this.service = service;
		this.generator = generator;
	}

    /**
     * Gets the chunk at the specified X and Z coordinates, loading it from the
     * disk or generating it if necessary.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The chunk.
     */
	public GlowChunk getChunk(int x, int z) {
		GlowChunk.Key key = new GlowChunk.Key(x, z);
		GlowChunk chunk = chunks.get(key);
		if (chunk == null) {
			try {
				chunk = service.read(world, x, z);
			} catch (IOException e) {
				chunk = null;
			}

			if (chunk == null) {
				chunk = generator.generate(world, x, z);
			}

			chunks.put(key, chunk);
		}
		return chunk;
	}
    
    /**
     * Forces generation of the given chunk.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk was successfully regenerated.
     */
    public boolean forceRegeneration(int x, int z) {
		GlowChunk.Key key = new GlowChunk.Key(x, z);
        GlowChunk chunk = generator.generate(world, x, z);
        if (chunk == null || !unloadChunk(x, z, false)) {
            return false;
        }
        chunks.put(key, chunk);
        return true;
    }
    
    /**
     * Checks whether the given Chunk is loaded.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk was loaded.
     */
    public boolean isLoaded(int x, int z) {
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        return chunks.get(key) != null;
    }
    
    /**
     * Unloads a given chunk from memory.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param save Whether to save the chunk if needed.
     * @return Whether the chunk was unloaded successfully.
     */
    public boolean unloadChunk(int x, int z, boolean save) {
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        GlowChunk chunk = chunks.get(key);
        if (chunk != null) {
            if (save) {
                try {
                    service.write(x, z, chunk);
                } catch (IOException ex) {
                    GlowServer.logger.log(Level.SEVERE, "Error while saving chunk: {0}", ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            }
            chunks.remove(key);
            return true;
        }
        return false;
    }
    
    /**
     * Gets a list of loaded chunks.
     * @return The currently loaded chunks.
     */
    public GlowChunk[] getLoadedChunks() {
        return chunks.values().toArray(new GlowChunk[]{});
    }
    
    /**
     * Force-saves the given chunk.
     * @param x The X coordinate.
     * @param Z The Z coordinate.
     */
    public boolean forceSave(int x, int z) {
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        GlowChunk chunk = chunks.get(key);
        if (chunk != null) {
            try {
                service.write(x, z, chunk);
            } catch (IOException ex) {
                GlowServer.logger.log(Level.SEVERE, "Error while saving chunk: {0}", ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    /**
     * Gets the seed of the world generator.
     * @return The seed.
     */
    public long getSeed() {
        return generator.getSeed();
    }

}
