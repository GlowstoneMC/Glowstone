package net.glowstone.constants;

import static org.bukkit.block.Biome.BEACHES;
import static org.bukkit.block.Biome.BIRCH_FOREST;
import static org.bukkit.block.Biome.BIRCH_FOREST_HILLS;
import static org.bukkit.block.Biome.COLD_BEACH;
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
import static org.bukkit.block.Biome.PLAINS;
import static org.bukkit.block.Biome.REDWOOD_TAIGA;
import static org.bukkit.block.Biome.REDWOOD_TAIGA_HILLS;
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
import static org.bukkit.block.Biome.values;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Data;
import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class GlowBiomeClimate {

    private static final Map<Biome, BiomeClimate> CLIMATE_MAP = new HashMap<>();
    private static final SimplexOctaveGenerator noiseGen;

    static {
        setBiomeClimate(BiomeClimate.DEFAULT, values());
        setBiomeClimate(BiomeClimate.PLAINS, PLAINS, MUTATED_PLAINS, BEACHES);
        setBiomeClimate(BiomeClimate.DESERT, DESERT, DESERT_HILLS, MUTATED_DESERT, MESA,
            MUTATED_MESA, MESA_CLEAR_ROCK, MESA_ROCK, MUTATED_MESA_CLEAR_ROCK, MUTATED_MESA_ROCK,
            HELL);
        setBiomeClimate(BiomeClimate.EXTREME_HILLS, EXTREME_HILLS, EXTREME_HILLS_WITH_TREES,
            MUTATED_EXTREME_HILLS, MUTATED_EXTREME_HILLS_WITH_TREES, STONE_BEACH,
            SMALLER_EXTREME_HILLS);
        setBiomeClimate(BiomeClimate.FOREST, FOREST, FOREST_HILLS, MUTATED_FOREST, ROOFED_FOREST,
            MUTATED_ROOFED_FOREST);
        setBiomeClimate(BiomeClimate.BIRCH_FOREST, BIRCH_FOREST, BIRCH_FOREST_HILLS,
            MUTATED_BIRCH_FOREST, MUTATED_BIRCH_FOREST_HILLS);
        setBiomeClimate(BiomeClimate.TAIGA, TAIGA, TAIGA_HILLS, MUTATED_TAIGA,
            MUTATED_REDWOOD_TAIGA, MUTATED_REDWOOD_TAIGA_HILLS);
        setBiomeClimate(BiomeClimate.SWAMPLAND, SWAMPLAND, MUTATED_SWAMPLAND);
        setBiomeClimate(BiomeClimate.ICE_PLAINS, ICE_FLATS, ICE_MOUNTAINS, MUTATED_ICE_FLATS,
            FROZEN_RIVER, FROZEN_OCEAN);
        setBiomeClimate(BiomeClimate.MUSHROOM, MUSHROOM_ISLAND, MUSHROOM_ISLAND_SHORE);
        setBiomeClimate(BiomeClimate.COLD_BEACH, COLD_BEACH);
        setBiomeClimate(BiomeClimate.JUNGLE, JUNGLE, JUNGLE_HILLS, MUTATED_JUNGLE);
        setBiomeClimate(BiomeClimate.JUNGLE_EDGE, JUNGLE_EDGE, MUTATED_JUNGLE_EDGE);
        setBiomeClimate(BiomeClimate.COLD_TAIGA, TAIGA_COLD, TAIGA_COLD_HILLS, MUTATED_TAIGA_COLD);
        setBiomeClimate(BiomeClimate.MEGA_TAIGA, REDWOOD_TAIGA, REDWOOD_TAIGA_HILLS);
        setBiomeClimate(BiomeClimate.SAVANNA, SAVANNA);
        setBiomeClimate(BiomeClimate.SAVANNA_MOUNTAINS, MUTATED_SAVANNA);
        setBiomeClimate(BiomeClimate.SAVANNA_PLATEAU, SAVANNA_ROCK);
        setBiomeClimate(BiomeClimate.SAVANNA_PLATEAU_MOUNTAINS, MUTATED_SAVANNA_ROCK);
        setBiomeClimate(BiomeClimate.SKY, SKY);

        noiseGen = new SimplexOctaveGenerator(new Random(1234), 1);
        noiseGen.setScale(1 / 8.0D);
    }

    public static double getBiomeTemperature(Biome biome) {
        return CLIMATE_MAP.get(biome).getTemperature();
    }

    public static double getBiomeHumidity(Biome biome) {
        return CLIMATE_MAP.get(biome).getHumidity();
    }

    public static double getTemperature(Block block) {
        return getBiomeTemperature(block.getBiome());
    }

    public static double getHumidity(Block block) {
        return getBiomeHumidity(block.getBiome());
    }

    public static boolean isWet(Block block) {
        return getBiomeHumidity(block.getBiome()) > 0.85D;
    }

    public static boolean isCold(Biome biome, int x, int y, int z) {
        return getVariatedTemperature(biome, x, y, z) < 0.15D;
    }

    public static boolean isCold(Block block) {
        return isCold(block.getBiome(), block.getX(), block.getY(), block.getZ());
    }

    public static boolean isRainy(Biome biome, int x, int y, int z) {
        boolean rainy = CLIMATE_MAP.get(biome).isRainy();
        return rainy && !isCold(biome, x, y, z);
    }

    public static boolean isRainy(Block block) {
        return isRainy(block.getBiome(), block.getX(), block.getY(), block.getZ());
    }

    public static boolean isSnowy(Biome biome, int x, int y, int z) {
        boolean rainy = CLIMATE_MAP.get(biome).isRainy();
        return rainy && isCold(biome, x, y, z);
    }

    public static boolean isSnowy(Block block) {
        return isSnowy(block.getBiome(), block.getX(), block.getY(), block.getZ());
    }

    private static double getVariatedTemperature(Biome biome, int x, int y, int z) {
        double temp = CLIMATE_MAP.get(biome).getTemperature();
        if (y > 64) {
            double variation = noiseGen.noise(x, z, 0.5D, 2.0D) * 4.0D;
            return temp - (variation + (y - 64)) * 0.05D / 30.0D;
        } else {
            return temp;
        }
    }

    private static void setBiomeClimate(BiomeClimate temp, Biome... biomes) {
        for (Biome biome : biomes) {
            CLIMATE_MAP.put(biome, temp);
        }
    }

    @Data
    private static class BiomeClimate {

        public static final BiomeClimate DEFAULT = new BiomeClimate(0.5D, 0.5D, true);
        public static final BiomeClimate PLAINS = new BiomeClimate(0.8D, 0.4D, true);
        public static final BiomeClimate DESERT = new BiomeClimate(2.0D, 0.0D, false);
        public static final BiomeClimate EXTREME_HILLS = new BiomeClimate(0.2D, 0.3D, true);
        public static final BiomeClimate FOREST = new BiomeClimate(0.7D, 0.8D, true);
        public static final BiomeClimate BIRCH_FOREST = new BiomeClimate(0.6D, 0.6D, true);
        public static final BiomeClimate TAIGA = new BiomeClimate(0.25D, 0.8D, true);
        public static final BiomeClimate SWAMPLAND = new BiomeClimate(0.8D, 0.9D, true);
        public static final BiomeClimate ICE_PLAINS = new BiomeClimate(0.0D, 0.5D, true);
        public static final BiomeClimate MUSHROOM = new BiomeClimate(0.9D, 1.0D, true);
        public static final BiomeClimate COLD_BEACH = new BiomeClimate(0.05D, 0.3D, true);
        public static final BiomeClimate JUNGLE = new BiomeClimate(0.95D, 0.9D, true);
        public static final BiomeClimate JUNGLE_EDGE = new BiomeClimate(0.95D, 0.8D, true);
        public static final BiomeClimate COLD_TAIGA = new BiomeClimate(-0.5D, 0.4D, true);
        public static final BiomeClimate MEGA_TAIGA = new BiomeClimate(0.3D, 0.8D, true);
        public static final BiomeClimate SAVANNA = new BiomeClimate(1.2D, 0.0D, false);
        public static final BiomeClimate SAVANNA_MOUNTAINS = new BiomeClimate(1.1D, 0.0D, false);
        public static final BiomeClimate SAVANNA_PLATEAU = new BiomeClimate(1.0D, 0.0D, false);
        public static final BiomeClimate SAVANNA_PLATEAU_MOUNTAINS = new BiomeClimate(0.5D, 0.0D,
            false);
        public static final BiomeClimate SKY = new BiomeClimate(0.5D, 0.5D, false);

        private final double temperature;
        private final double humidity;
        private final boolean rainy;
    }
}
