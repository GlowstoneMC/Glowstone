package net.glowstone;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.BlockPopulator;

import net.glowstone.io.ChunkIoService;

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
     * The chunk generator used to generate new chunks.
     */
	private final ChunkGenerator generator;
    
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
	public ChunkManager(GlowWorld world, ChunkIoService service, ChunkGenerator generator) {
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
                chunk = new GlowChunk(world, x, z);
				byte[] data = generator.generate(world, new Random(), x, z);
                chunk.setTypes(data);
                
                chunks.put(key, chunk);
                
                for (int x2 = x - 1; x2 <= x + 1; ++x2) {
                    for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                        if (canPopulate(x2, z2)) {
                            GlowChunk chunk2 = getChunk(x2, z2);
                            chunk2.setPopulated(true);
                            
                            for (BlockPopulator p : world.getPopulators()) {
                                p.populate(world, new Random(), chunk2);
                                System.out.println("pop " + world.getName() + " " + p.getClass().getName());
                            }
                        }
                    }
                }
			}

			chunks.put(key, chunk);
		}
        
		return chunk;
	}
    
    /**
     * Checks whether the given chunk can be populated by map features.
     * @return Whether population is needed and safe.
     */
    private boolean canPopulate(int x, int z) {
        if (isLoaded(x, z)) {
            if (getChunk(x, z).getPopulated()) return false;
        } else {
            return false;
        }
        for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                if (!isLoaded(x2, z2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Forces generation of the given chunk.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk was successfully regenerated.
     */
    public boolean forceRegeneration(int x, int z) {
		GlowChunk.Key key = new GlowChunk.Key(x, z);
        GlowChunk chunk = new GlowChunk(world, x, z);
        chunk.setTypes(generator.generate(world, new Random(), x, z));
        
        if (chunk == null || !unloadChunk(x, z, false)) {
            return false;
        }
        
        if (canPopulate(x, z)) {
            chunk.setPopulated(true);
            for (BlockPopulator p : world.getPopulators()) {
                p.populate(world, new Random(), chunk);
            }
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
     * Get the chunk generator.
     */
    public ChunkGenerator getGenerator() {
        return generator;
    }

}
