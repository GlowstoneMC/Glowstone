package net.glowstone.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Base chunk generator class.
 */
public abstract class GlowChunkGenerator extends ChunkGenerator {

    // distinct from GlowChunk.DEPTH, only used in the wgen
    protected static final int WORLD_DEPTH = 128;

    private static final Set<Material> noSpawnFloors = EnumSet.of(Material.FIRE, Material.CACTUS);
    private final Map<String, Map<String, OctaveGenerator>> octaveCache = new HashMap<>();
    private final List<BlockPopulator> populators;

    protected GlowChunkGenerator(BlockPopulator... args) {
        populators = Arrays.asList(args);
    }

    /**
     * Populates a Map with the {@link OctaveGenerator} instances for the world.
     *
     * @param world The world to create OctaveGenerators for
     * @param octaves The map to put the OctaveGenerators into
     */
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
    }

    /**
     * Returns the {@link OctaveGenerator} instances for the world, which are either newly created
     * or retrieved from the cache.
     *
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

    @Override
    public final List<BlockPopulator> getDefaultPopulators(World world) {
        return populators;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
        return !block.isLiquid() && !block.isEmpty() && !noSpawnFloors.contains(block.getType());
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int spawnX = random.nextInt(256) - 128;
        int spawnZ = random.nextInt(256) - 128;

        for (int tries = 0; tries < 1000 && !canSpawn(world, spawnX, spawnZ); ++tries) {
            spawnX += random.nextInt(128) - 64;
            spawnZ += random.nextInt(128) - 64;
        }

        return new Location(world, spawnX, world.getHighestBlockYAt(spawnX, spawnZ), spawnZ);
    }
}
