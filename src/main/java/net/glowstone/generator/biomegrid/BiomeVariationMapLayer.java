package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.block.Biome.BADLANDS;
import static org.bukkit.block.Biome.DARK_FOREST;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.FOREST;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.PLAINS;
import static org.bukkit.block.Biome.SAVANNA;
import static org.bukkit.block.Biome.SAVANNA_PLATEAU;

public class BiomeVariationMapLayer extends MapLayer {

    private static final int[] ISLANDS = new int[]{GlowBiome.getId(PLAINS),
        GlowBiome.getId(FOREST)};
    private static final Map<Integer, int[]> VARIATIONS = new HashMap<>();

    static {
        VARIATIONS.put(GlowBiome.getId(DARK_FOREST), new int[]{GlowBiome.getId(PLAINS)});
        VARIATIONS.put(GlowBiome.getId(PLAINS), new int[]{GlowBiome.getId(FOREST), GlowBiome.getId(FOREST)});
        VARIATIONS.put(GlowBiome.getId(OCEAN), new int[]{GlowBiome.getId(DEEP_OCEAN)});
        VARIATIONS.put(GlowBiome.getId(SAVANNA), new int[]{GlowBiome.getId(SAVANNA_PLATEAU)});
        VARIATIONS.put(GlowBiome.getId(BADLANDS), new int[]{GlowBiome.getId(BADLANDS)});
    }

    private final MapLayer belowLayer;
    private final MapLayer variationLayer;

    /**
     * Creates an instance with no variation layer.
     * @param seed the PRNG seed
     * @param belowLayer the layer below this one
     */
    public BiomeVariationMapLayer(long seed, MapLayer belowLayer) {
        this(seed, belowLayer, null);
    }

    /**
     * Creates an instance with the given variation layer.
     * @param seed the PRNG seed
     * @param belowLayer the layer below this one
     * @param variationLayer the variation layer, or null to use no variation layer
     */
    public BiomeVariationMapLayer(long seed, MapLayer belowLayer, MapLayer variationLayer) {
        super(seed);
        this.belowLayer = belowLayer;
        this.variationLayer = variationLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (variationLayer == null) {
            return generateRandomValues(x, z, sizeX, sizeZ);
        }
        return mergeValues(x, z, sizeX, sizeZ);
    }

    /**
     * Generates a rectangle, replacing all the positive values in the previous layer with random
     * values from 2 to 31 while leaving zero and negative values unchanged.
     *
     * @param x the lowest x coordinate
     * @param z the lowest z coordinate
     * @param sizeX the x coordinate range
     * @param sizeZ the z coordinate range
     * @return a flattened array of generated values
     */
    public int[] generateRandomValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                int val = values[j + i * sizeX];
                if (val > 0) {
                    setCoordsSeed(x + j, z + i);
                    val = nextInt(30) + 2;
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }

    /**
     * Generates a rectangle using the previous layer and the variation layer.
     *
     * @param x the lowest x coordinate
     * @param z the lowest z coordinate
     * @param sizeX the x coordinate range
     * @param sizeZ the z coordinate range
     * @return a flattened array of generated values
     */
    public int[] mergeValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;

        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);
        int[] variationValues = variationLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                setCoordsSeed(x + j, z + i);
                int centerValue = values[j + 1 + (i + 1) * gridSizeX];
                int variationValue = variationValues[j + 1 + (i + 1) * gridSizeX];
                if (centerValue != 0 && variationValue == 3 && centerValue < 128) {
                    finalValues[j + i * sizeX] =
                        GlowBiome.getBiome(centerValue + 128) != null ? centerValue + 128
                            : centerValue;
                } else if (variationValue == 2 || nextInt(3) == 0) {
                    int val = centerValue;
                    if (VARIATIONS.containsKey(centerValue)) {
                        val = VARIATIONS.get(centerValue)[nextInt(
                            VARIATIONS.get(centerValue).length)];
                    } else if (centerValue == GlowBiome.getId(DEEP_OCEAN) && nextInt(3) == 0) {
                        val = ISLANDS[nextInt(ISLANDS.length)];
                    }
                    if (variationValue == 2 && val != centerValue) {
                        val = GlowBiome.getBiome(val + 128) != null ? val + 128 : centerValue;
                    }
                    if (val != centerValue) {
                        int count = 0;
                        if (values[j + 1 + i * gridSizeX] == centerValue) { // upper value
                            count++;
                        }
                        if (values[j + 1 + (i + 2) * gridSizeX] == centerValue) { // lower value
                            count++;
                        }
                        if (values[j + (i + 1) * gridSizeX] == centerValue) { // left value
                            count++;
                        }
                        if (values[j + 2 + (i + 1) * gridSizeX] == centerValue) { // right value
                            count++;
                        }
                        // spread mountains if not too close from an edge
                        finalValues[j + i * sizeX] = count < 3 ? centerValue : val;
                    } else {
                        finalValues[j + i * sizeX] = val;
                    }
                } else {
                    finalValues[j + i * sizeX] = centerValue;
                }
            }
        }
        return finalValues;
    }
}
