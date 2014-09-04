package net.glowstone.generator;

import net.glowstone.GlowChunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;

import java.util.*;

/**
 * Base chunk generator class.
 */
public abstract class GlowChunkGenerator extends ChunkGenerator {

    // distinct from GlowChunk.DEPTH, only used in the wgen
    protected static final int WORLD_DEPTH = 128;

    private static final Set<Material> noSpawnFloors = new HashSet<>(Arrays.asList(Material.FIRE, Material.CACTUS, Material.LEAVES));
    private final Map<String, Map<String, OctaveGenerator>> octaveCache = new HashMap<>();
    private final List<BlockPopulator> populators;

    protected GlowChunkGenerator(BlockPopulator... args) {
        populators = Arrays.asList(args);
    }

    /**
     * @param world The world to create OctaveGenerators for
     * @param octaves The map to put the OctaveGenerators into
     */
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
    }

    /**
     * @param world The world to look for in the cache
     * @return A map of {@link OctaveGenerator}s created by {@link #createWorldOctaves(World, Map)}
     */
    protected final Map<String, OctaveGenerator> getWorldOctaves(World world) {
        if (octaveCache.get(world.getName()) == null) {
            Map<String, OctaveGenerator> octaves = new HashMap<>();
            createWorldOctaves(world, octaves);
            octaveCache.put(world.getName(), octaves);
            return octaves;
        }
        return octaveCache.get(world.getName());
    }

    /**
     * Create a new byte[] buffer of the proper size.
     * @param fill The Material to fill with.
     * @return A new filled byte[16 * 16 * 128];
     */
    protected byte[] start(Material fill) {
        if (fill == null) {
            throw new IllegalArgumentException("Invalid block type!");
        }
        byte[] data = new byte[GlowChunk.HEIGHT * GlowChunk.WIDTH * WORLD_DEPTH];
        Arrays.fill(data, (byte) fill.getId());
        return data;
    }

    /**
     * Set the given block to the given type.
     * @param data The buffer to write to.
     * @param x The chunk X coordinate.
     * @param y The Y coordinate.
     * @param z The chunk Z coordinate.
     * @param id The block type.
     */
    protected void set(byte[] data, int x, int y, int z, Material id) {
        if (data == null) {
            throw new IllegalStateException();
        }
        if (id == null) {
            throw new IllegalArgumentException("Unknown block type!");
        }
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) {
            return;
        }
        data[(x * 16 + z) * 128 + y] = (byte) id.getId();
    }

    /**
     * Get the given block type.
     * @param data The buffer to read from.
     * @param x The chunk X coordinate.
     * @param y The Y coordinate.
     * @param z The chunk Z coordinate.
     * @return The type of block at the location.
     */
    protected Material get(byte[] data, int x, int y, int z) {
        if (data == null) {
            throw new IllegalStateException();
        }
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) {
            return Material.AIR;
        }
        return Material.getMaterial(data[(x * 16 + z) * 128 + y]);
    }

    @Override
    public final List<BlockPopulator> getDefaultPopulators(World world) {
        return populators;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
        return !block.isLiquid() && !block.isEmpty() && !noSpawnFloors.contains(block.getType());
    }
    
}
