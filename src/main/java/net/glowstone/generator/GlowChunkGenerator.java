package net.glowstone.generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;

import net.glowstone.GlowChunk;

/**
 * Base chunk generator class.
 */
public abstract class GlowChunkGenerator extends ChunkGenerator {

	private static final Set<Material> noSpawnFloors = new HashSet<Material>(Arrays.asList(Material.FIRE, Material.CACTUS, Material.LEAVES));
    
	private final Map<String, Map<String, OctaveGenerator>> octaveCache = new HashMap<String, Map<String, OctaveGenerator>>();
	private final List<BlockPopulator> populators;
    
    protected GlowChunkGenerator(BlockPopulator... args) {
        populators = Arrays.asList(args);
    }

	/**
	 * @param world The world to create OctaveGenerators for
	 * @param octaves The map to put the OctaveGenerators into
	 */
	protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {}

	/**
	 * @param world The world to look for in the cache
	 * @return A map of {@link OctaveGenerator}s created by {@link #createWorldOctaves(World, Map)}
	 */
	protected final Map<String, OctaveGenerator> getWorldOctaves(World world) {
		if (octaveCache.get(world.getName()) == null) {
			Map<String, OctaveGenerator> octaves = new HashMap<String, OctaveGenerator>();
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
        byte[] data = new byte[GlowChunk.HEIGHT * GlowChunk.WIDTH * GlowChunk.DEPTH];
        Arrays.fill(data, (byte) fill.getId());
        return data;
    }
    
    /**
     * Set the given block to the given type.
     * @param data The buffer to write to.
     * @param x The chunk X coordinate.
     * @param y The Y coordinate.
     * @param z The chunk Z coordinate.
     * @param mat The block type.
     */
    protected void set(byte[] data, int x, int y, int z, Material mat) {
        if (data == null) throw new IllegalStateException();
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) return;
        data[(x * GlowChunk.HEIGHT + z) * GlowChunk.DEPTH + y] = (byte) mat.getId();
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
        if (data == null) throw new IllegalStateException();
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) return Material.AIR;
        return Material.getMaterial(data[(x * GlowChunk.HEIGHT + z) * GlowChunk.DEPTH + y]);
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
