package net.glowstone;

import net.glowstone.io.ChunkIoService;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

/**
 * A class which manages the {@link GlowChunk}s currently loaded in memory.
 * @author Graham Edgecombe
 */
public final class ChunkManager {

    /**
     * The world this ChunkManager is managing.
     */
    private final GlowWorld world;

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
     * A map of chunks currently loaded in memory.
     */
    private final ConcurrentMap<GlowChunk.Key, GlowChunk> chunks = new ConcurrentHashMap<GlowChunk.Key, GlowChunk>();

    /**
     * A map of chunks which are being kept loaded by players or other factors.
     */
    private final ConcurrentMap<GlowChunk.Key, Set<ChunkLock>> locks = new ConcurrentHashMap<GlowChunk.Key, Set<ChunkLock>>();

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
     * Get the chunk generator.
     */
    public ChunkGenerator getGenerator() {
        return generator;
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
        // a simple new GlowChunk() does not allocate significant memory
        return getOrCreate(chunks, key, new GlowChunk(world, x, z));
    }

    /**
     * Call the ChunkIoService to load a chunk, optionally generating the chunk.
     * @param x The X coordinate of the chunk to load.
     * @param z The Y coordinate of the chunk to load.
     * @param generate Whether to generate the chunk if needed.
     * @return True on success, false on failure.
     */
    public boolean loadChunk(int x, int z, boolean generate) {
        GlowChunk chunk = getChunk(x, z);

        // try to load chunk
        try {
            if (service.read(chunk, x, z)) {
                EventFactory.onChunkLoad(chunk, false);
                return true;
            }
        } catch (Exception e) {
            GlowServer.logger.log(Level.SEVERE, "Error while loading chunk (" + x + "," + z + ")", e);
        }

        // stop here if we can't generate
        if (!generate) {
            return false;
        }

        // get generating
        try {
            generateChunk(chunk, x, z);
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Error while generating chunk (" + x + "," + z + ")", ex);
            return false;
        }

        EventFactory.onChunkLoad(chunk, true);

        for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                populateChunk(x2, z2);
            }
        }
        return true;
    }

    /**
     * Check whether a chunk has locks on it preventing it from being unloaded.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk is in use.
     */
    public boolean isChunkInUse(int x, int z) {
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        Set<ChunkLock> lockSet = locks.get(key);
        return lockSet != null && lockSet.size() != 0;
    }

    /**
     * Unload chunks with no locks on them.
     */
    public void unloadOldChunks() {
        for (Map.Entry<GlowChunk.Key, GlowChunk> entry : chunks.entrySet()) {
            Set<ChunkLock> lockSet = locks.get(entry.getKey());
            if (lockSet == null || lockSet.size() == 0) {
                boolean result = entry.getValue().unload(true, true);
                GlowServer.logger.info("Unloading unused " + entry.getKey() + ": " + result);
            }
            if (!entry.getValue().isLoaded()) {
                GlowServer.logger.info("Removing from cache " + entry.getKey());
                chunks.entrySet().remove(entry);
                locks.remove(entry.getKey());
            }
        }
    }

    /**
     * Populate a single chunk if needed.
     */
    private void populateChunk(int x, int z) {
        GlowChunk chunk = getChunk(x, z);
        // cancel out if it's already loaded or populated
        if (!chunk.isLoaded() || chunk.getPopulated()) {
            return;
        }

        // cancel out if the 3x3 around it isn't available
        for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                if (!getChunk(x2, z2).isLoaded()) {
                    return;
                }
            }
        }

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
     * Get the size of the chunk object cache.
     * @return The size of the chunk cache.
     */
    public int getCacheSize() {
        return chunks.size();
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

    private Set<ChunkLock> getLockSet(GlowChunk.Key key) {
        return getOrCreate(locks, key, new HashSet<ChunkLock>());
    }

    /**
     * Helper method for getting or creating a value in ConcurrentMap.
     */
    private <K, V> V getOrCreate(ConcurrentMap<K, V> map, K key, V def) {
        V prev = map.putIfAbsent(key, def);
        return prev == null ? def : prev;
    }

    /**
     * A group of locks on chunks to prevent them from being unloaded while in use.
     */
    public static class ChunkLock implements Iterable<GlowChunk.Key> {
        private final ChunkManager cm;
        private final Set<GlowChunk.Key> keys = new HashSet<GlowChunk.Key>();

        public ChunkLock(ChunkManager cm) {
            this.cm = cm;
        }

        public void acquire(GlowChunk.Key key) {
            if (keys.contains(key)) return;
            keys.add(key);
            cm.getLockSet(key).add(this);
        }

        public void release(GlowChunk.Key key) {
            if (!keys.contains(key)) return;
            keys.remove(key);
            cm.getLockSet(key).remove(this);
        }

        public void clear() {
            for (GlowChunk.Key key : keys) {
                cm.getLockSet(key).remove(this);
            }
            keys.clear();
        }

        public Iterator<GlowChunk.Key> iterator() {
            return keys.iterator();
        }
    }
}
