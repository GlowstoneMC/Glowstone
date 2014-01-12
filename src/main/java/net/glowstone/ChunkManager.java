package net.glowstone;

import net.glowstone.io.ChunkIoService;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

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
     * A Random object to be used to generate chunks.
     */
    private final Random chunkRandom = new Random();
    
    /**
     * A Random object to be used to populate chunks.
     */
    private final Random popRandom = new Random();

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
            chunk = new GlowChunk(world, x, z);
            chunks.put(key, chunk);
        }
        return chunk;
    }
    
    /**
     * Call the ChunkIoService to load a chunk, optionally generating the chunk.
     * @param x The X coordinate of the chunk to load.
     * @param z The Y coordinate of the chunk to load.
     * @param generate Whether to generate the chunk if needed.
     * @return True on success, false on failure.
     */
    public boolean loadChunk(int x, int z, boolean generate) {
        boolean success;
        try {
            success = service.read(getChunk(x, z), x, z);
        } catch (Exception e) {
            GlowServer.logger.log(Level.SEVERE, "Error while loading chunk ({0},{1})", new Object[]{x, z});
            e.printStackTrace();
            success = false;
        }
        EventFactory.onChunkLoad(getChunk(x, z), !success);
        if (!success && generate) {
            try {
                generateChunk(getChunk(x, z), x, z);
            }
            catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error while generating chunk ({0},{1})", new Object[]{x, z});
                ex.printStackTrace();
                return false;
            }

            for (int x2 = x - 1; x2 <= x + 1; ++x2) {
                for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                    populateChunk(x2, z2);
                }
            }
            return true;
        }
        
        return success;
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
     * Populate a single chunk if needed.
     */
    private void populateChunk(int x, int z) {
        if (!canPopulate(x, z)) return;

        GlowChunk chunk = getChunk(x, z);
        chunk.setPopulated(true);

        popRandom.setSeed(world.getSeed());
        long xRand = popRandom.nextLong() / 2 * 2 + 1;
        long zRand = popRandom.nextLong() / 2 * 2 + 1;
        popRandom.setSeed((long) x * xRand + (long) z * zRand ^ world.getSeed());

        for (BlockPopulator p : world.getPopulators()) {
            p.populate(world, popRandom, chunk);
        }

        EventFactory.onChunkPopulate(chunk);
    }

    /**
     * Initialize a single chunk from the chunk generator.
     */
    private void generateChunk(GlowChunk chunk, int x, int z) {
        chunkRandom.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkGenerator.BiomeGrid biomes = new BiomeGrid(x, z);

        // try for extended sections
        short[][] extSections = generator.generateExtBlockSections(world, chunkRandom, x, z, biomes);
        if (extSections != null) {
            throw new UnsupportedOperationException("Extended chunk sections not yet supported");
        }

        // normal sections
        byte[][] sections = generator.generateBlockSections(world, chunkRandom, x, z, biomes);
        if (sections != null) {
            GlowChunk.ChunkSection[] chunkSections = new GlowChunk.ChunkSection[sections.length];
            for (int i = 0; i < sections.length; ++i) {
                // this is sort of messy.
                chunkSections[i] = new GlowChunk.ChunkSection();
                System.arraycopy(sections[i], 0, chunkSections[i].types, 0, chunkSections[i].types.length);
            }
            chunk.initializeSections(chunkSections);
            return;
        }

        // deprecated flat generation
        chunk.initializeTypes(generator.generate(world, chunkRandom, x, z));
    }
    
    /**
     * Forces generation of the given chunk.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk was successfully regenerated.
     */
    public boolean forceRegeneration(int x, int z) {
        GlowChunk chunk = getChunk(x, z);
        
        if (chunk == null || !chunk.unload(false, false)) {
            return false;
        }

        chunk.setPopulated(false);
        generateChunk(chunk, x, z);
        populateChunk(x, z);
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
     * Gets a list of loaded chunks.
     * @return The currently loaded chunks.
     */
    public GlowChunk[] getLoadedChunks() {
        ArrayList<GlowChunk> result = new ArrayList<GlowChunk>();
        for (GlowChunk chunk : chunks.values()) {
            if (chunk.isLoaded()) {
                result.add(chunk);
            }
        }
        return result.toArray(new GlowChunk[result.size()]);
    }
    
    /**
     * Force-saves the given chunk.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public boolean forceSave(int x, int z) {
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        GlowChunk chunk = chunks.get(key);
        if (chunk != null) {
            try {
                service.write(x, z, chunk);
                return true;
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

    /**
     * A BiomeGrid implementation for chunk generation.
     */
    private class BiomeGrid implements ChunkGenerator.BiomeGrid {
        private final int cx, cz;
        public BiomeGrid(int x, int z) {
            cx = x;
            cz = z;
        }

        public Biome getBiome(int x, int z) {
            return world.getBiome((cx << 4) | x, (cz << 4) | z);
        }

        public void setBiome(int x, int z, Biome bio) {
            world.setBiome((cx << 4) | x, (cz << 4) | z, bio);
        }
    }
}
