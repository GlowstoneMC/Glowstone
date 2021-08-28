package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.FROZEN_RIVER;
import static org.bukkit.block.Biome.MUSHROOM_FIELDS;
import static org.bukkit.block.Biome.MUSHROOM_FIELD_SHORE;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.RIVER;
import static org.bukkit.block.Biome.SNOWY_TUNDRA;

public class RiverMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> SPECIAL_RIVERS = new HashMap<>();
    private static final int CLEAR_VALUE = 0;
    private static final int RIVER_VALUE = 1;

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        SPECIAL_RIVERS.put(GlowBiome.getId(SNOWY_TUNDRA), GlowBiome.getId(FROZEN_RIVER));
        SPECIAL_RIVERS
            .put(GlowBiome.getId(MUSHROOM_FIELDS), GlowBiome.getId(MUSHROOM_FIELD_SHORE));
        SPECIAL_RIVERS
            .put(GlowBiome.getId(MUSHROOM_FIELD_SHORE), GlowBiome.getId(MUSHROOM_FIELD_SHORE));
    }

    private final MapLayer belowLayer;
    private final MapLayer mergeLayer;

    public RiverMapLayer(long seed, MapLayer belowLayer) {
        this(seed, belowLayer, null);
    }

    /**
     * Creates a map layer that generates rivers.
     *
     * @param seed the layer's PRNG seed
     * @param belowLayer the layer to apply before this one
     * @param mergeLayer TODO: document this parameter
     */
    public RiverMapLayer(long seed, MapLayer belowLayer, MapLayer mergeLayer) {
        super(seed);
        this.belowLayer = belowLayer;
        this.mergeLayer = mergeLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (mergeLayer == null) {
            return generateRivers(x, z, sizeX, sizeZ);
        }
        return mergeRivers(x, z, sizeX, sizeZ);
    }

    private int[] generateRivers(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;

        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                // This applies rivers using Von Neumann neighborhood
                int centerVal = values[j + 1 + (i + 1) * gridSizeX] & 1;
                int upperVal = values[j + 1 + i * gridSizeX] & 1;
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX] & 1;
                int leftVal = values[j + (i + 1) * gridSizeX] & 1;
                int rightVal = values[j + 2 + (i + 1) * gridSizeX] & 1;
                int val = CLEAR_VALUE;
                if (centerVal != upperVal || centerVal != lowerVal || centerVal != leftVal
                    || centerVal != rightVal) {
                    val = RIVER_VALUE;
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }

    private int[] mergeRivers(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] mergeValues = mergeLayer.generateValues(x, z, sizeX, sizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeX * sizeZ; i++) {
            int val = mergeValues[i];
            if (OCEANS.contains(mergeValues[i])) {
                val = mergeValues[i];
            } else if (values[i] == RIVER_VALUE) {
                if (SPECIAL_RIVERS.containsKey(mergeValues[i])) {
                    val = SPECIAL_RIVERS.get(mergeValues[i]);
                } else {
                    val = GlowBiome.getId(RIVER);
                }
            }
            finalValues[i] = val;
        }

        return finalValues;
    }
}
