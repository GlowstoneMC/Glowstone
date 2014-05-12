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
    private final ConcurrentMap<GlowChunk.Key, GlowChunk> chunks = new ConcurrentHashMap<>();

    /**
     * A map of chunks which are being kept loaded by players or other factors.
     */
    private final ConcurrentMap<GlowChunk.Key, Set<ChunkLock>> locks = new ConcurrentHashMap<>();

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
            if (service.read(chunk)) {
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

        // right now, forcePopulate takes care of populating chunks that players actually see.
        /*for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                populateChunk(x2, z2, false);
            }
        }*/
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
                if (!entry.getValue().unload(true, true)) {
                    GlowServer.logger.warning("Failed to unload chunk " + world.getName() + ":" + entry.getKey());
                }
            }
            // cannot remove old chunks from cache - GlowBlock and GlowBlockState keep references.
            // they must either be changed to look up the chunk again all the time, or this code left out.
            /*if (!entry.getValue().isLoaded()) {
                //GlowServer.logger.info("Removing from cache " + entry.getKey());
                chunks.entrySet().remove(entry);
                locks.remove(entry.getKey());
            }*/
        }
    }

    /**
     * Populate a single chunk if needed.
     */
    private void populateChunk(int x, int z, boolean force) {
        GlowChunk chunk = getChunk(x, z);
        // cancel out if it's already populated
        if (chunk.isPopulated()) {
            return;
        }

        // cancel out if the 3x3 around it isn't available
        for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                if (!getChunk(x2, z2).isLoaded() && (!force || !loadChunk(x2, z2, true))) {
                    return;
                }
            }
        }

        // it might have loaded since before, so check again that it's not already populated
        if (chunk.isPopulated()) {
            return;
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
     * Force a chunk to be populated by loading the chunks in an area around it. Used when streaming chunks to players
     * so that they do not have to watch chunks being populated.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public void forcePopulation(int x, int z) {
        populateChunk(x, z, true);
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
        byte[][] blockSections = generator.generateBlockSections(world, chunkRandom, x, z, biomes);
        if (blockSections != null) {
            GlowChunk.ChunkSection[] sections = new GlowChunk.ChunkSection[blockSections.length];
            for (int i = 0; i < blockSections.length; ++i) {
                // this is sort of messy.
                sections[i] = new GlowChunk.ChunkSection();
                System.arraycopy(blockSections[i], 0, sections[i].types, 0, sections[i].types.length);
            }
            chunk.initializeSections(sections);
            return;
        }

        // deprecated flat generation
        byte[] types = generator.generate(world, chunkRandom, x, z);
        //GlowServer.logger.warning("Using deprecated generate() in generator: " + generator.getClass().getName());

        GlowChunk.ChunkSection[] sections = new GlowChunk.ChunkSection[8];
        for (int sy = 0; sy < sections.length; ++sy) {
            GlowChunk.ChunkSection sec = new GlowChunk.ChunkSection();
            int by = 16 * sy;
            for (int cx = 0; cx < 16; ++cx) {
                for (int cz = 0; cz < 16; ++cz) {
                    for (int cy = by; cy < by + 16; ++cy) {
                        sec.types[sec.index(cx, cy, cz)] = types[(cx * 16 + cz) * 128 + cy];
                    }
                }
            }
            sections[sy] = sec;
        }
        chunk.initializeSections(sections);
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
        populateChunk(x, z, false);  // should this be forced?
        return true;
    }

    /**
     * Gets a list of loaded chunks.
     * @return The currently loaded chunks.
     */
    public GlowChunk[] getLoadedChunks() {
        ArrayList<GlowChunk> result = new ArrayList<>();
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
        GlowChunk chunk = getChunk(x, z);
        if (chunk.isLoaded()) {
            try {
                service.write(chunk);
                return true;
            } catch (IOException ex) {
                GlowServer.logger.log(Level.SEVERE, "Error while saving chunk (" + x + ", " + z + ")", ex);
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
        private final String desc;
        private final Set<GlowChunk.Key> keys = new HashSet<>();

        public ChunkLock(ChunkManager cm, String desc) {
            this.cm = cm;
            this.desc = desc;
        }

        public void acquire(GlowChunk.Key key) {
            if (keys.contains(key)) return;
            keys.add(key);
            cm.getLockSet(key).add(this);
            //GlowServer.logger.info(this + " acquires " + key);
        }

        public void release(GlowChunk.Key key) {
            if (!keys.contains(key)) return;
            keys.remove(key);
            cm.getLockSet(key).remove(this);
            //GlowServer.logger.info(this + " releases " + key);
        }

        public void clear() {
            for (GlowChunk.Key key : keys) {
                cm.getLockSet(key).remove(this);
                //GlowServer.logger.info(this + " clearing " + key);
            }
            keys.clear();
        }

        @Override
        public String toString() {
            return "ChunkLock{" + desc + "}";
        }

        public Iterator<GlowChunk.Key> iterator() {
            return keys.iterator();
        }
    }
}
