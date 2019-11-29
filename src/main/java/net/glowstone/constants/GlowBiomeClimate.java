package net.glowstone.constants;

import static org.bukkit.block.Biome.values;

import java.util.EnumMap;
import java.util.Random;
import lombok.Data;
import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

// TODO: replce with Biome builder (including generation info, etc.)
@Deprecated
public class GlowBiomeClimate {

    private static final EnumMap<Biome, BiomeClimate> CLIMATE_MAP = new EnumMap<>(Biome.class);
    private static final SimplexOctaveGenerator noiseGen;

    static {
        setBiomeClimate(BiomeClimate.DEFAULT, values());

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

        private final double temperature;
        private final double humidity;
        private final boolean rainy;
    }
}
