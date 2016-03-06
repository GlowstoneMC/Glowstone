package net.glowstone.constants;

import org.bukkit.block.Biome;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.block.Biome.*;

/**
 * Mappings for Biome id values.
 */
public final class GlowBiome {

    private static final int[] ids = new int[values().length];
    private static final Biome[] biomes = new Biome[256];

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
        set(12, ICE_FLATS);
        set(13, ICE_MOUNTAINS);
        set(14, MUSHROOM_ISLAND);
        set(15, MUSHROOM_ISLAND_SHORE);
        set(16, BEACHES);
        set(17, DESERT_HILLS);
        set(18, FOREST_HILLS);
        set(19, TAIGA_HILLS);
        set(20, SMALLER_EXTREME_HILLS);
        set(21, JUNGLE);
        set(22, JUNGLE_HILLS);
        set(23, JUNGLE_EDGE);
        set(24, DEEP_OCEAN);
        set(25, STONE_BEACH);
        set(26, COLD_BEACH);
        set(27, BIRCH_FOREST);
        set(28, BIRCH_FOREST_HILLS);
        set(29, ROOFED_FOREST);
        set(30, TAIGA_COLD);
        set(31, TAIGA_COLD_HILLS);
        set(32, REDWOOD_TAIGA);
        set(33, REDWOOD_TAIGA_HILLS);
        set(34, EXTREME_HILLS_WITH_TREES);
        set(35, SAVANNA);
        set(36, SAVANNA_ROCK);
        set(37, MESA);
        set(38, MESA_ROCK);
        set(39, MESA_CLEAR_ROCK);
        set(127, VOID);
        set(129, MUTATED_PLAINS);
        set(130, MUTATED_DESERT);
        set(131, MUTATED_EXTREME_HILLS);
        set(132, MUTATED_FOREST);
        set(133, MUTATED_TAIGA);
        set(134, MUTATED_SWAMPLAND);
        set(140, MUTATED_ICE_FLATS);
        set(149, MUTATED_JUNGLE);
        set(151, MUTATED_JUNGLE_EDGE);
        set(155, MUTATED_BIRCH_FOREST);
        set(156, MUTATED_BIRCH_FOREST_HILLS);
        set(157, MUTATED_ROOFED_FOREST);
        set(158, MUTATED_TAIGA_COLD);
        set(160, MUTATED_REDWOOD_TAIGA);
        set(161, MUTATED_REDWOOD_TAIGA_HILLS);
        set(162, MUTATED_EXTREME_HILLS_WITH_TREES);
        set(163, MUTATED_SAVANNA);
        set(164, MUTATED_SAVANNA_ROCK);
        set(165, MUTATED_MESA);
        set(166, MUTATED_MESA_ROCK);
        set(167, MUTATED_MESA_CLEAR_ROCK);
    }

    private GlowBiome() {

    }

    /**
     * Get the biome ID for a specified Biome.
     *
     * @param biome the Biome.
     * @return the biome id, or -1
     */
    public static int getId(Biome biome) {
        checkNotNull(biome, "Biome cannot be null");
        return ids[biome.ordinal()];
    }

    /**
     * Get the Biome for a specified id.
     *
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

}
