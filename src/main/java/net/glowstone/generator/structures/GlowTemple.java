package net.glowstone.generator.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Desert temple, jungle temple, or witch hut.
 */
public class GlowTemple extends GlowStructure {

    private static final int MIN_DISTANCE = 8;
    private static final int MAX_DISTANCE = 32;
    private final Map<Biome, TempleType> types = new HashMap<>();

    /**
     * Creates a structure with no pieces.
     *
     * @param world the world to generate in
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     */
    public GlowTemple(World world, int chunkX, int chunkZ) {
        super(world, chunkX, chunkZ);
        types.put(Biome.DESERT, TempleType.DESERT_TEMPLE);
        types.put(Biome.DESERT_HILLS, TempleType.DESERT_TEMPLE);
        types.put(Biome.JUNGLE, TempleType.JUNGLE_TEMPLE);
        types.put(Biome.JUNGLE_HILLS, TempleType.JUNGLE_TEMPLE);
        types.put(Biome.SWAMP, TempleType.WITCH_HUT);
    }

    /**
     * Creates a random temple or witch hut.
     *
     * @param world the world to generate in
     * @param random the PRNG that will choose this temple's orientation
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     */
    public GlowTemple(World world, Random random, int chunkX, int chunkZ) {
        this(world, chunkX, chunkZ);

        int x = chunkX << 4;
        int z = chunkZ << 4;
        Biome biome = world.getBiome(x + 8, z + 8);

        if (types.containsKey(biome)) {
            switch (types.get(biome)) {
                case JUNGLE_TEMPLE:
                    addPiece(new GlowJungleTemple(random,
                        new Location(world, x, world.getSeaLevel(), z)));
                    break;
                case WITCH_HUT:
                    addPiece(
                        new GlowWitchHut(random, new Location(world, x, world.getSeaLevel(), z)));
                    break;
                default:
                    addPiece(new GlowDesertTemple(random,
                        new Location(world, x, world.getSeaLevel(), z)));
            }
            wrapAllPieces();
        }
    }

    @Override
    public boolean shouldGenerate(Random random) {
        Biome biome = world.getBiome((chunkX << 4) + 8, (chunkZ << 4) + 8);
        if (types.containsKey(biome)) {
            int x = chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE;
            int z = chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE;
            x = x * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
            z = z * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
            if (x == chunkX && z == chunkZ) {
                return true;
            }
        }
        return false;
    }

    public enum TempleType {
        DESERT_TEMPLE,
        JUNGLE_TEMPLE,
        WITCH_HUT
    }
}
