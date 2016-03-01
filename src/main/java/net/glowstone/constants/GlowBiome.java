package net.glowstone.constants;

import org.apache.commons.lang3.Validate;
import org.bukkit.block.Biome;

import java.util.Arrays;

import static org.bukkit.block.Biome.*;

/**
 * Mappings for Biome id values.
 */
public final class GlowBiome {

    private static final int[] ids = new int[Biome.values().length];
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
        set(13, ICE_MOUNTAINS);
        set(14, MUSHROOM_ISLAND);
        set(17, DESERT_HILLS);
        set(18, FOREST_HILLS);
        set(19, TAIGA_HILLS);
        set(21, JUNGLE);
        set(22, JUNGLE_HILLS);
        set(23, JUNGLE_EDGE);
        set(24, DEEP_OCEAN);
        set(25, STONE_BEACH);
        set(26, COLD_BEACH);
        set(27, BIRCH_FOREST);
        set(28, BIRCH_FOREST_HILLS);
        set(29, ROOFED_FOREST);
        set(35, SAVANNA);
        set(37, MESA);
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
        Validate.notNull(biome, "Biome cannot be null");
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
