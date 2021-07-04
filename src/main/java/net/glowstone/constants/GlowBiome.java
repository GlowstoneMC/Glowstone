package net.glowstone.constants;

import net.glowstone.i18n.ConsoleMessages;
import org.bukkit.block.Biome;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.block.Biome.BEACHES;
import static org.bukkit.block.Biome.BIRCH_FOREST;
import static org.bukkit.block.Biome.BIRCH_FOREST_HILLS;
import static org.bukkit.block.Biome.COLD_BEACH;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.DESERT_HILLS;
import static org.bukkit.block.Biome.EXTREME_HILLS;
import static org.bukkit.block.Biome.EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.FOREST;
import static org.bukkit.block.Biome.FOREST_HILLS;
import static org.bukkit.block.Biome.FROZEN_OCEAN;
import static org.bukkit.block.Biome.FROZEN_RIVER;
import static org.bukkit.block.Biome.HELL;
import static org.bukkit.block.Biome.ICE_FLATS;
import static org.bukkit.block.Biome.ICE_MOUNTAINS;
import static org.bukkit.block.Biome.JUNGLE;
import static org.bukkit.block.Biome.JUNGLE_EDGE;
import static org.bukkit.block.Biome.JUNGLE_HILLS;
import static org.bukkit.block.Biome.MESA;
import static org.bukkit.block.Biome.MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MESA_ROCK;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND_SHORE;
import static org.bukkit.block.Biome.MUTATED_BIRCH_FOREST;
import static org.bukkit.block.Biome.MUTATED_BIRCH_FOREST_HILLS;
import static org.bukkit.block.Biome.MUTATED_DESERT;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.MUTATED_FOREST;
import static org.bukkit.block.Biome.MUTATED_ICE_FLATS;
import static org.bukkit.block.Biome.MUTATED_JUNGLE;
import static org.bukkit.block.Biome.MUTATED_JUNGLE_EDGE;
import static org.bukkit.block.Biome.MUTATED_MESA;
import static org.bukkit.block.Biome.MUTATED_MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MUTATED_MESA_ROCK;
import static org.bukkit.block.Biome.MUTATED_PLAINS;
import static org.bukkit.block.Biome.MUTATED_REDWOOD_TAIGA;
import static org.bukkit.block.Biome.MUTATED_REDWOOD_TAIGA_HILLS;
import static org.bukkit.block.Biome.MUTATED_ROOFED_FOREST;
import static org.bukkit.block.Biome.MUTATED_SAVANNA;
import static org.bukkit.block.Biome.MUTATED_SAVANNA_ROCK;
import static org.bukkit.block.Biome.MUTATED_SWAMPLAND;
import static org.bukkit.block.Biome.MUTATED_TAIGA;
import static org.bukkit.block.Biome.MUTATED_TAIGA_COLD;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.PLAINS;
import static org.bukkit.block.Biome.REDWOOD_TAIGA;
import static org.bukkit.block.Biome.REDWOOD_TAIGA_HILLS;
import static org.bukkit.block.Biome.RIVER;
import static org.bukkit.block.Biome.ROOFED_FOREST;
import static org.bukkit.block.Biome.SAVANNA;
import static org.bukkit.block.Biome.SAVANNA_ROCK;
import static org.bukkit.block.Biome.SKY;
import static org.bukkit.block.Biome.SMALLER_EXTREME_HILLS;
import static org.bukkit.block.Biome.STONE_BEACH;
import static org.bukkit.block.Biome.SWAMPLAND;
import static org.bukkit.block.Biome.TAIGA;
import static org.bukkit.block.Biome.TAIGA_COLD;
import static org.bukkit.block.Biome.TAIGA_COLD_HILLS;
import static org.bukkit.block.Biome.TAIGA_HILLS;
import static org.bukkit.block.Biome.VOID;
import static org.bukkit.block.Biome.values;

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
        if (id < biomes.length) {
            return biomes[id];
        } else {
            ConsoleMessages.Error.Biome.UNKNOWN.log(id);
            return null;
        }
    }

    private static void set(int id, Biome biome) {
        ids[biome.ordinal()] = id;
        biomes[id] = biome;
    }

}
