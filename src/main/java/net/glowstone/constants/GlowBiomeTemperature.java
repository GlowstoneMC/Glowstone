package net.glowstone.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.block.Biome;

import net.glowstone.util.noise.SimplexOctaveGenerator;

import static org.bukkit.block.Biome.*;

public class GlowBiomeTemperature {
    private static final Map<Biome, BiomeTemperature> TEMPERATURE_MAP = new HashMap<>();
    private static final SimplexOctaveGenerator noiseGen;

    public static double getBiomeTemperature(Biome biome) {
        return TEMPERATURE_MAP.get(biome).getTemperature();
    }

    public static double getBiomeHumidity(Biome biome) {
        return TEMPERATURE_MAP.get(biome).getHumidity();
    }

    public static double getVariatedTemperature(Biome biome, int x, int y, int z) {
        double temp = TEMPERATURE_MAP.get(biome).getTemperature();
        if (y > 64) {
            double variation = noiseGen.noise(x, z, 0.5D, 2.0D) * 4.0D;
            return temp - (variation + (double) (y - 64)) * 0.05D / 30.0D;
        } else {
            return temp;
        }
    }

    private static void setBiomeTemperature(BiomeTemperature temp, Biome... biomes) {
        for (Biome biome : biomes) {
            TEMPERATURE_MAP.put(biome, temp);
        }
    }

    private static class BiomeTemperature {
        public static final BiomeTemperature DEFAULT = new BiomeTemperature(0.5D, 0.5D);
        public static final BiomeTemperature PLAINS = new BiomeTemperature(0.8D, 0.4D);
        public static final BiomeTemperature DESERT = new BiomeTemperature(2.0D, 0.0D);
        public static final BiomeTemperature EXTREME_HILLS = new BiomeTemperature(0.2D, 0.3D);
        public static final BiomeTemperature FOREST = new BiomeTemperature(0.7D, 0.8D);
        public static final BiomeTemperature BIRCH_FOREST = new BiomeTemperature(0.6D, 0.6D);
        public static final BiomeTemperature TAIGA = new BiomeTemperature(0.25D, 0.8D);
        public static final BiomeTemperature SWAMPLAND = new BiomeTemperature(0.8D, 0.9D);
        public static final BiomeTemperature ICE_PLAINS = new BiomeTemperature(0.0D, 0.5D);
        public static final BiomeTemperature MUSHROOM = new BiomeTemperature(0.9D, 1.0D);
        public static final BiomeTemperature COLD_BEACH = new BiomeTemperature(0.05D, 0.3D);
        public static final BiomeTemperature JUNGLE = new BiomeTemperature(0.95D, 0.9D);
        public static final BiomeTemperature JUNGLE_EDGE = new BiomeTemperature(0.95D, 0.8D);
        public static final BiomeTemperature COLD_TAIGA = new BiomeTemperature(-0.5D, 0.4D);
        public static final BiomeTemperature MEGA_TAIGA = new BiomeTemperature(0.3D, 0.8D);
        public static final BiomeTemperature SAVANNA = new BiomeTemperature(1.2D, 0.0D);
        public static final BiomeTemperature SAVANNA_MOUNTAINS = new BiomeTemperature(1.1D, 0.0D);
        public static final BiomeTemperature SAVANNA_PLATEAU = new BiomeTemperature(1.0D, 0.0D);
        public static final BiomeTemperature SAVANNA_PLATEAU_MOUNTAINS = new BiomeTemperature(0.5D, 0.0D);

        private final double temperature;
        private final double humidity;

        public BiomeTemperature(double temperature, double humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getHumidity() {
            return humidity;
        }
    }

    static {
        setBiomeTemperature(BiomeTemperature.DEFAULT, Biome.values());
        setBiomeTemperature(BiomeTemperature.PLAINS, PLAINS, SUNFLOWER_PLAINS, BEACH);
        setBiomeTemperature(BiomeTemperature.DESERT, DESERT, DESERT_HILLS, DESERT_MOUNTAINS, MESA, MESA_BRYCE, MESA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU_MOUNTAINS, MESA_PLATEAU_FOREST_MOUNTAINS, HELL);
        setBiomeTemperature(BiomeTemperature.EXTREME_HILLS, EXTREME_HILLS, EXTREME_HILLS_PLUS, EXTREME_HILLS_MOUNTAINS, EXTREME_HILLS_PLUS_MOUNTAINS, STONE_BEACH, SMALL_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.FOREST, FOREST, FOREST_HILLS, FLOWER_FOREST, ROOFED_FOREST, ROOFED_FOREST_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.BIRCH_FOREST, BIRCH_FOREST, BIRCH_FOREST_HILLS, BIRCH_FOREST_MOUNTAINS, BIRCH_FOREST_HILLS_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.TAIGA, TAIGA, TAIGA_HILLS, TAIGA_MOUNTAINS, MEGA_SPRUCE_TAIGA, MEGA_SPRUCE_TAIGA_HILLS);
        setBiomeTemperature(BiomeTemperature.SWAMPLAND, SWAMPLAND, SWAMPLAND_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.ICE_PLAINS, ICE_PLAINS, ICE_MOUNTAINS, ICE_PLAINS_SPIKES, FROZEN_RIVER, FROZEN_OCEAN);
        setBiomeTemperature(BiomeTemperature.MUSHROOM, MUSHROOM_ISLAND, MUSHROOM_SHORE);
        setBiomeTemperature(BiomeTemperature.COLD_BEACH, COLD_BEACH);
        setBiomeTemperature(BiomeTemperature.JUNGLE, JUNGLE, JUNGLE_HILLS, JUNGLE_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.JUNGLE_EDGE, JUNGLE_EDGE, JUNGLE_EDGE_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.COLD_TAIGA, COLD_TAIGA, COLD_TAIGA_HILLS, COLD_TAIGA_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.MEGA_TAIGA, MEGA_TAIGA, MEGA_TAIGA_HILLS);
        setBiomeTemperature(BiomeTemperature.SAVANNA, SAVANNA);
        setBiomeTemperature(BiomeTemperature.SAVANNA_MOUNTAINS, SAVANNA_MOUNTAINS);
        setBiomeTemperature(BiomeTemperature.SAVANNA_PLATEAU, SAVANNA_PLATEAU);
        setBiomeTemperature(BiomeTemperature.SAVANNA_PLATEAU_MOUNTAINS, SAVANNA_PLATEAU_MOUNTAINS);

        noiseGen = new SimplexOctaveGenerator(new Random(1234), 1);
        noiseGen.setScale(1 / 8.0D);
    }
}
