package net.glowstone.constants;

import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.block.Biome.*;

public class GlowBiomeClimate {
    private static final Map<Biome, BiomeClimate> CLIMATE_MAP = new HashMap<>();
    private static final SimplexOctaveGenerator noiseGen;

    static {
        setBiomeClimate(BiomeClimate.DEFAULT, Biome.values());
        setBiomeClimate(BiomeClimate.PLAINS, PLAINS, SUNFLOWER_PLAINS, BEACH);
        setBiomeClimate(BiomeClimate.DESERT, DESERT, DESERT_HILLS, DESERT_MOUNTAINS, MESA, MESA_BRYCE, MESA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU_MOUNTAINS, MESA_PLATEAU_FOREST_MOUNTAINS, HELL);
        setBiomeClimate(BiomeClimate.EXTREME_HILLS, EXTREME_HILLS, EXTREME_HILLS_PLUS, EXTREME_HILLS_MOUNTAINS, EXTREME_HILLS_PLUS_MOUNTAINS, STONE_BEACH, SMALL_MOUNTAINS);
        setBiomeClimate(BiomeClimate.FOREST, FOREST, FOREST_HILLS, FLOWER_FOREST, ROOFED_FOREST, ROOFED_FOREST_MOUNTAINS);
        setBiomeClimate(BiomeClimate.BIRCH_FOREST, BIRCH_FOREST, BIRCH_FOREST_HILLS, BIRCH_FOREST_MOUNTAINS, BIRCH_FOREST_HILLS_MOUNTAINS);
        setBiomeClimate(BiomeClimate.TAIGA, TAIGA, TAIGA_HILLS, TAIGA_MOUNTAINS, MEGA_SPRUCE_TAIGA, MEGA_SPRUCE_TAIGA_HILLS);
        setBiomeClimate(BiomeClimate.SWAMPLAND, SWAMPLAND, SWAMPLAND_MOUNTAINS);
        setBiomeClimate(BiomeClimate.ICE_PLAINS, ICE_PLAINS, ICE_MOUNTAINS, ICE_PLAINS_SPIKES, FROZEN_RIVER, FROZEN_OCEAN);
        setBiomeClimate(BiomeClimate.MUSHROOM, MUSHROOM_ISLAND, MUSHROOM_SHORE);
        setBiomeClimate(BiomeClimate.COLD_BEACH, COLD_BEACH);
        setBiomeClimate(BiomeClimate.JUNGLE, JUNGLE, JUNGLE_HILLS, JUNGLE_MOUNTAINS);
        setBiomeClimate(BiomeClimate.JUNGLE_EDGE, JUNGLE_EDGE, JUNGLE_EDGE_MOUNTAINS);
        setBiomeClimate(BiomeClimate.COLD_TAIGA, COLD_TAIGA, COLD_TAIGA_HILLS, COLD_TAIGA_MOUNTAINS);
        setBiomeClimate(BiomeClimate.MEGA_TAIGA, MEGA_TAIGA, MEGA_TAIGA_HILLS);
        setBiomeClimate(BiomeClimate.SAVANNA, SAVANNA);
        setBiomeClimate(BiomeClimate.SAVANNA_MOUNTAINS, SAVANNA_MOUNTAINS);
        setBiomeClimate(BiomeClimate.SAVANNA_PLATEAU, SAVANNA_PLATEAU);
        setBiomeClimate(BiomeClimate.SAVANNA_PLATEAU_MOUNTAINS, SAVANNA_PLATEAU_MOUNTAINS);
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
        public static final BiomeClimate SAVANNA_PLATEAU_MOUNTAINS = new BiomeClimate(0.5D, 0.0D, false);
        public static final BiomeClimate SKY = new BiomeClimate(0.5D, 0.5D, false);

        private final double temperature;
        private final double humidity;
        private final boolean rainy;

        public BiomeClimate(double temperature, double humidity, boolean rainy) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.rainy = rainy;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public boolean isRainy() {
            return rainy;
        }
    }
}
