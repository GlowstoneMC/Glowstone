package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;

import java.util.Arrays;

import static org.bukkit.block.Biome.*;

/**
 * Mappings for Biome id values.
 */
public final class GlowBiome {

    private GlowBiome() {}

    private static final int[] ids = new int[Biome.values().length];
    private static final Biome[] biomes = new Biome[256];

    /**
     * Get the biome ID for a specified Biome.
     * @param biome the Biome.
     * @return the biome id, or -1
     */
    public static int getId(Biome biome) {
        Validate.notNull(biome, "Biome cannot be null");
        return ids[biome.ordinal()];
    }

    /**
     * Get the Biome for a specified id.
     * @param id the id.
     * @return the Biome, or null
     */
    public static Biome getBiome(int id) {
        return biomes[id];
    }

    private static void set(int id, Biome biome) {
        ids[biome.ordinal()] = id;
        biomes[id] = biome;
    }

    static {
        Arrays.fill(ids, -1);
        set(0, OCEAN);
        set(1, PLAINS);
        set(2, DESERT);
        set(3, EXTREME_HILLS);
        set(4, FOREST);
        set(5, TAIGA);
        set(6, SWAMPLAND);
        set(7, RIVER);
        set(8, HELL);
        set(9, SKY);
        set(10, FROZEN_OCEAN);
        set(11, FROZEN_RIVER);
        set(12, ICE_PLAINS);
        set(13, ICE_MOUNTAINS);
        set(14, MUSHROOM_ISLAND);
        set(15, MUSHROOM_SHORE);
        set(16, BEACH);
        set(17, DESERT_HILLS);
        set(18, FOREST_HILLS);
        set(19, TAIGA_HILLS);
        set(20, SMALL_MOUNTAINS); // EXTREME_HILLS_EDGE
        set(21, JUNGLE);
        set(22, JUNGLE_HILLS);
        set(23, JUNGLE_EDGE);
        set(24, DEEP_OCEAN);
        set(25, STONE_BEACH);
        set(26, COLD_BEACH);
        set(27, BIRCH_FOREST);
        set(28, BIRCH_FOREST_HILLS);
        set(29, ROOFED_FOREST);
        set(30, COLD_TAIGA);
        set(31, COLD_TAIGA_HILLS);
        set(32, MEGA_TAIGA);
        set(33, MEGA_TAIGA_HILLS);
        set(34, EXTREME_HILLS_PLUS);
        set(35, SAVANNA);
        set(36, SAVANNA_PLATEAU);
        set(37, MESA);
        set(38, MESA_PLATEAU_FOREST);
        set(39, MESA_PLATEAU);
        set(129, SUNFLOWER_PLAINS);
        set(130, DESERT_MOUNTAINS);
        set(131, EXTREME_HILLS_MOUNTAINS);
        set(132, FLOWER_FOREST);
        set(133, TAIGA_MOUNTAINS);
        set(134, SWAMPLAND_MOUNTAINS);
        set(140, ICE_PLAINS_SPIKES);
        set(149, JUNGLE_MOUNTAINS);
        set(151, JUNGLE_EDGE_MOUNTAINS);
        set(155, BIRCH_FOREST_MOUNTAINS);
        set(156, BIRCH_FOREST_HILLS_MOUNTAINS);
        set(157, ROOFED_FOREST_MOUNTAINS);
        set(158, COLD_TAIGA_MOUNTAINS);
        set(160, MEGA_SPRUCE_TAIGA);
        set(161, MEGA_SPRUCE_TAIGA_HILLS);
        set(162, EXTREME_HILLS_PLUS_MOUNTAINS);
        set(163, SAVANNA_MOUNTAINS);
        set(164, SAVANNA_PLATEAU_MOUNTAINS);
        set(165, MESA_BRYCE);
        set(166, MESA_PLATEAU_FOREST_MOUNTAINS);
        set(167, MESA_PLATEAU_MOUNTAINS);
    }

}
