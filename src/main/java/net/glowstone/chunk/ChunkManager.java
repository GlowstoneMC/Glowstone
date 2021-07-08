package net.glowstone.chunk;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.chunk.GlowChunk.Key;
import net.glowstone.constants.GlowBiome;
import net.glowstone.generator.GlowChunkData;
import net.glowstone.generator.GlowChunkGenerator;
import net.glowstone.generator.biomegrid.MapLayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.io.ChunkIoService;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A class which manages the {@link GlowChunk}s currently loaded in memory.
 *
 * @author Graham Edgecombe
 */
public final class ChunkManager {

    /**
     * The world this ChunkManager is managing.
     */
    private final GlowWorld world;

    /**
     * The chunk I/O service used to read chunks from the disk and write them to the disk.
     */
    private final ChunkIoService service;

    /**
     * The chunk generator used to generate new chunks.
     *
     * @return the chunk generator
     */
    @Getter
    private final ChunkGenerator generator;

    /**
     * The biome maps used to fill chunks biome grid and terrain generation.
     */
    private final MapLayer[] biomeGrid;

    /**
     * A map of chunks currently loaded in memory.
     */
    private final ConcurrentMap<Key, GlowChunk> chunks = new ConcurrentHashMap<>();

    private final Multimap<Key, BlockChangeMessage> blockUpdates = MultimapBuilder.hashKeys().hashSetValues().build();

    /**
     * A set of chunks which are being kept loaded by players or other factors.
     */
    private final Multiset<Key> lockSet = ConcurrentHashMultiset.create();

    /**
     * Creates a new chunk manager with the specified I/O service and world generator.
     *
     * @param world The chunk manager's world.
     * @param service The I/O service.
     * @param generator The world generator.
     */
    public ChunkManager(GlowWorld world, ChunkIoService service, ChunkGenerator generator) {
        this.world = world;
        this.service = service;
        this.generator = generator;
        biomeGrid = MapLayer.initialize(
                world.getSeed(), world.getEnvironment(), world.getWorldType());
    }

    /**
     * Gets a chunk object representing the specified coordinates, which might not yet be loaded.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The chunk.
     */
    public GlowChunk getChunk(int x, int z) {
        Key key = GlowChunk.Key.of(x, z);
        return getChunk(key);
    }

    /**
     * Gets a chunk object from its key, which might not yet be loaded.
     *
     * @param key The x, y key of the chunk.
     * @return The chunk.
     */
    public GlowChunk getChunk(GlowChunk.Key key) {
        GlowChunk chunk = chunks.get(key);
        if (chunk == null) {
            // only create chunk if it's not in the map already
            chunk = new GlowChunk(world, key.getX(), key.getZ());
            chunks.put(key, chunk);
        }
        return chunk;
    }

    /**
     * Checks if the Chunk at the specified coordinates is loaded.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return true if the chunk is loaded, otherwise false.
     */
    public boolean isChunkLoaded(int x, int z) {
        Key key = GlowChunk.Key.of(x, z);
        return chunks.containsKey(key) && chunks.get(key).isLoaded();
    }

    /**
     * Check whether a chunk has locks on it preventing it from being unloaded.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return Whether the chunk is in use.
     */
    public boolean isChunkInUse(int x, int z) {
        Key key = GlowChunk.Key.of(x, z);
        return lockSet.contains(key);
    }

    /**
     * Call the ChunkIoService to load a chunk, optionally generating the chunk.
     *
     * @param x The X coordinate of the chunk to load.
     * @param z The Y coordinate of the chunk to load.
     * @param generate Whether to generate the chunk if needed.
     * @return True on success, false on failure.
     */
    public boolean loadChunk(int x, int z, boolean generate) {
        return loadChunk(getChunk(x, z), generate);
    }

    /**
     * Attempts to load a chunk; handles exceptions.
     * @param chunk the chunk address
     * @param generate if true, generate the chunk if it's new or the saved copy is corrupted
     * @return true if the chunk was loaded or generated successfully, false otherwise
     */
    public boolean loadChunk(GlowChunk chunk, boolean generate) {
        // try to load chunk
        try {
            if (service.read(chunk)) {
                EventFactory.getInstance()
                        .callEvent(new ChunkLoadEvent(chunk, false));
                return true;
            }
        } catch (IOException e) {
            ConsoleMessages.Error.Chunk.LOAD_FAILED.log(e, chunk.getX(), chunk.getZ());
            // an error in chunk reading may have left the chunk in an invalid state
            // (i.e. double initialization errors), so it's forcibly unloaded here
            chunk.unload(false, false);
        }

        // stop here if we can't generate
        if (!generate || world.getServer().isGenerationDisabled()) {
            return false;
        }

        // get generating
        try {
            generateChunk(chunk, chunk.getX(), chunk.getZ());
        } catch (Throwable ex) {
            ConsoleMessages.Error.Chunk.GEN_FAILED.log(ex, chunk.getX(), chunk.getZ());
            return false;
        }

        EventFactory.getInstance().callEvent(new ChunkLoadEvent(chunk, true));

        // right now, forcePopulate takes care of populating chunks that players actually see.
        /*for (int x2 = x - 1; x2 <= x + 1; ++x2) {
            for (int z2 = z - 1; z2 <= z + 1; ++z2) {
                populateChunk(x2, z2, false);
            }
        }*/
        return true;
    }

    /**
     * Unload chunks with no locks on them.
     */
    public void unloadOldChunks() {
        Iterator<Entry<Key, GlowChunk>> chunksEntryIter = chunks.entrySet().iterator();
        while (chunksEntryIter.hasNext()) {
            Entry<Key, GlowChunk> entry = chunksEntryIter.next();
            if (!lockSet.contains(entry.getKey())) {
                if (!entry.getValue().unload(true, true)) {
                    ConsoleMessages.Warn.Chunk.UNLOAD_FAILED.log(world.getName(), entry.getKey());
                }
            }
            if (!entry.getValue().isLoaded()) {
                //GlowServer.logger.info("Removing from cache " + entry.getKey());
                chunksEntryIter.remove();
                lockSet.setCount(entry.getKey(), 0);
            }
        }
    }

    /**
     * Populate a single chunk if needed.
     */
    private void populateChunk(int x, int z, boolean force) {
        if (world.getServer().isGenerationDisabled()) {
            return;
        }
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

        Random random = new Random(world.getSeed());
        long xrand = (random.nextLong() / 2 << 1) + 1;
        long zrand = (random.nextLong() / 2 << 1) + 1;
        random.setSeed(x * xrand + z * zrand ^ world.getSeed());

        for (BlockPopulator p : world.getPopulators()) {
            p.populate(world, random, chunk);
        }

        EventFactory.getInstance().callEvent(new ChunkPopulateEvent(chunk));
    }

    /**
     * Force a chunk to be populated by loading the chunks in an area around it. Used when streaming
     * chunks to players so that they do not have to watch chunks being populated.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public void forcePopulation(int x, int z) {
        try {
            populateChunk(x, z, true);
        } catch (Throwable ex) {
            ConsoleMessages.Error.Chunk.POP_FAILED.log(ex, x, z);
        }
    }

    /**
     * Initialize a single chunk from the chunk generator.
     */
    private void generateChunk(GlowChunk chunk, int x, int z) {
        Random random = new Random(x * 341873128712L + z * 132897987541L);
        BiomeGrid biomes = new BiomeGrid();

        int[] biomeValues = biomeGrid[0].generateValues(
                x * GlowChunk.WIDTH, z * GlowChunk.HEIGHT, GlowChunk.WIDTH, GlowChunk.HEIGHT);
        for (int i = 0; i < biomeValues.length; i++) {
            biomes.biomes[i] = (byte) biomeValues[i];
        }

        // extended sections with data
        GlowChunkData glowChunkData = null;
        if (generator instanceof GlowChunkGenerator) {
            glowChunkData = (GlowChunkData)
                    generator.generateChunkData(world, random, x, z, biomes);
        } else {
            ChunkGenerator.ChunkData chunkData =
                    generator.generateChunkData(world, random, x, z, biomes);
            if (chunkData != null) {
                glowChunkData = new GlowChunkData(world);
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        int maxHeight = chunkData.getMaxHeight();
                        for (int k = 0; k < maxHeight; ++k) {
                            MaterialData materialData = chunkData.getTypeAndData(i, k, j);
                            glowChunkData.setBlock(i, k, j, materialData == null
                                    ? new MaterialData(Material.AIR) : materialData);
                        }
                    }
                }
            }
        }

        if (glowChunkData != null) {
            short[][] extSections = glowChunkData.getSections();
            if (extSections != null) {
                ChunkSection[] sections = new ChunkSection[extSections.length];
                for (int i = 0; i < extSections.length; ++i) {
                    if (extSections[i] != null) {
                        sections[i] = ChunkSection.fromStateArray(extSections[i]);
                    }
                }
                chunk.initializeSections(sections);
                chunk.setBiomes(biomes.biomes);
                chunk.automaticHeightMap();
                return;
            }
        }

        // extended sections
        short[][] extSections = generator.generateExtBlockSections(world, random, x, z, biomes);
        if (extSections != null) {
            ChunkSection[] sections = new ChunkSection[extSections.length];
            for (int i = 0; i < extSections.length; ++i) {
                if (extSections[i] != null) {
                    sections[i] = ChunkSection.fromIdArray(extSections[i]);
                }
            }
            chunk.initializeSections(sections);
            chunk.setBiomes(biomes.biomes);
            chunk.automaticHeightMap();
            return;
        }

        // normal sections
        byte[][] blockSections = generator.generateBlockSections(world, random, x, z, biomes);
        if (blockSections != null) {
            ChunkSection[] sections = new ChunkSection[blockSections.length];
            for (int i = 0; i < blockSections.length; ++i) {
                if (blockSections[i] != null) {
                    sections[i] = ChunkSection.fromIdArray(blockSections[i]);
                }
            }
            chunk.initializeSections(sections);
            chunk.setBiomes(biomes.biomes);
            chunk.automaticHeightMap();
            return;
        }

        // deprecated flat generation
        byte[] types = generator.generate(world, random, x, z);
        ChunkSection[] sections = new ChunkSection[8];
        for (int sy = 0; sy < sections.length; ++sy) {
            // We can't use a normal constructor here due to the "interesting"
            // choices used for this deprecated API (blocks are in vertical columns)
            ChunkSection sec = new ChunkSection();
            int by = 16 * sy;
            for (int cx = 0; cx < 16; ++cx) {
                for (int cz = 0; cz < 16; ++cz) {
                    for (int cy = by; cy < by + 16; ++cy) {
                        char type = (char) types[(((cx << 4) + cz) << 7) + cy];
                        sec.setType(cx, cy, cz, (char) (type << 4));
                    }
                }
            }
            sections[sy] = sec;
        }
        chunk.initializeSections(sections);
        chunk.setBiomes(biomes.biomes);
        chunk.automaticHeightMap();
    }

    /**
     * Forces generation of the given chunk.
     *
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
        try {
            generateChunk(chunk, x, z);
            populateChunk(x, z, false);  // should this be forced?
        } catch (Throwable ex) {
            ConsoleMessages.Error.Chunk.REGEN_FAILED.log(ex, chunk.getX(), chunk.getZ());
            return false;
        }
        return true;
    }

    /**
     * Gets a list of loaded chunks.
     *
     * @return The currently loaded chunks.
     */
    public GlowChunk[] getLoadedChunks() {
        return chunks.values().stream().filter(GlowChunk::isLoaded).toArray(GlowChunk[]::new);
    }

    /**
     * Performs the save for the given chunk using the storage provider.
     *
     * @param chunk The chunk to save.
     * @return True if the save was successful.
     */
    public boolean performSave(GlowChunk chunk) {
        if (chunk.isLoaded()) {
            try {
                service.write(chunk);
                return true;
            } catch (IOException ex) {
                ConsoleMessages.Error.Chunk.SAVE_FAILED.log(ex, chunk);
                return false;
            }
        }
        return false;
    }

    public int[] getBiomeGridAtLowerRes(int x, int z, int sizeX, int sizeZ) {
        return biomeGrid[1].generateValues(x, z, sizeX, sizeZ);
    }

    public int[] getBiomeGrid(int x, int z, int sizeX, int sizeZ) {
        return biomeGrid[0].generateValues(x, z, sizeX, sizeZ);
    }

    /**
     * Queues block change notification to all players in this chunk
     *
     * @param key The chunk's key
     * @param message The block change message to broadcast
     */
    public void broadcastBlockChange(GlowChunk.Key key, BlockChangeMessage message) {
        blockUpdates.put(key, message);
    }

    /**
     * Queues block change notification to all players in this chunk
     *
     * @param key The chunk's key
     * @param messages The block change messages to broadcast
     */
    public void broadcastBlockChanges(GlowChunk.Key key, Iterable<BlockChangeMessage> messages) {
        blockUpdates.putAll(key, messages);
    }

    public List<BlockChangeMessage> getBlockChanges(GlowChunk.Key key) {
        return new ArrayList<>(blockUpdates.get(key));
    }

    public void clearChunkBlockChanges() {
        blockUpdates.clear();
    }

    /**
     * Indicates that a chunk should be locked. A chunk may be locked multiple times, and will only
     * be unloaded when all instances of a lock has been released.
     * @param key The chunk's key
     */
    private void acquireLock(Key key) {
        lockSet.add(key);
    }

    /**
     * Releases one instance of a chunk lock. A chunk may be locked multiple times, and will only be
     * unloaded when all instances of a lock has been released.
     * @param key The chunk's key
     */
    private void releaseLock(Key key) {
        lockSet.remove(key);
    }

    /**
     * A group of locks on chunks to prevent them from being unloaded while in use.
     */
    public static class ChunkLock implements Iterable<Key> {

        private final ChunkManager cm;
        private final String desc;
        private final Set<Key> keys = new HashSet<>();

        public ChunkLock(ChunkManager cm, String desc) {
            this.cm = cm;
            this.desc = desc;
        }

        /**
         * Acquires a lock on the given chunk key, if it's not already held.
         * @param key the key to lock
         */
        public void acquire(Key key) {
            if (keys.contains(key)) {
                return;
            }
            keys.add(key);
            cm.acquireLock(key);
            //GlowServer.logger.info(this + " acquires " + key);
        }

        /**
         * Releases a lock on the given chunk key, if it's not already held.
         * @param key the key to lock
         */
        public void release(Key key) {
            if (!keys.contains(key)) {
                return;
            }
            keys.remove(key);
            cm.releaseLock(key);
            //GlowServer.logger.info(this + " releases " + key);
        }

        /**
         * Release all locks.
         */
        public void clear() {
            for (Key key : keys) {
                cm.releaseLock(key);
                //GlowServer.logger.info(this + " clearing " + key);
            }
            keys.clear();
        }

        @Override
        public String toString() {
            return "ChunkLock{" + desc + "}";
        }

        @Override
        public Iterator<Key> iterator() {
            return keys.iterator();
        }
    }

    /**
     * A BiomeGrid implementation for chunk generation.
     */
    private static class BiomeGrid implements ChunkGenerator.BiomeGrid {

        private final byte[] biomes = new byte[256];

        @Override
        public Biome getBiome(int x, int z) {
            // upcasting is very important to get extended biomes
            return GlowBiome.getBiome(biomes[x | z << 4] & 0xFF);
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            biomes[x | z << 4] = (byte) GlowBiome.getId(bio);
        }
    }
}
