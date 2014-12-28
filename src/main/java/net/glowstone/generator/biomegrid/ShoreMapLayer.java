package net.glowstone.generator.biomegrid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.glowstone.constants.GlowBiome;

import static org.bukkit.block.Biome.*;

public class ShoreMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> SPECIAL_SHORES = new HashMap<>();
    private final MapLayer belowLayer;

    public ShoreMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                // This applies shores using Von Neumann neighborhood
                // it takes a 3x3 grid with a cross shape and analyzes values as follow
                // 0X0
                // XxX
                // 0X0
                // the grid center value decides how we are proceeding:
                // - if it's not ocean and it's surrounded by at least 1 ocean cell
                // it turns the center value into beach.
                int upperVal = values[j + 1 + i * gridSizeX];
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                int leftVal = values[j + (i + 1) * gridSizeX];
                int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (!OCEANS.contains(centerVal) && (OCEANS.contains(upperVal) || OCEANS.contains(lowerVal) ||
                        OCEANS.contains(leftVal) || OCEANS.contains(rightVal))) {
                    finalValues[j + i * sizeX] =
                            SPECIAL_SHORES.containsKey(centerVal) ? SPECIAL_SHORES.get(centerVal) : GlowBiome.getId(BEACH);
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS_PLUS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS_MOUNTAINS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS_PLUS_MOUNTAINS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_PLAINS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_MOUNTAINS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_PLAINS_SPIKES), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(COLD_TAIGA), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(COLD_TAIGA_HILLS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(COLD_TAIGA_MOUNTAINS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_MOUNTAINS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(MUSHROOM_ISLAND), GlowBiome.getId(MUSHROOM_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(SWAMPLAND), GlowBiome.getId(SWAMPLAND));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA), GlowBiome.getId(MESA));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_PLATEAU_FOREST), GlowBiome.getId(MESA_PLATEAU_FOREST));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_PLATEAU_FOREST_MOUNTAINS), GlowBiome.getId(MESA_PLATEAU_FOREST_MOUNTAINS));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_PLATEAU), GlowBiome.getId(MESA_PLATEAU));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_PLATEAU_MOUNTAINS), GlowBiome.getId(MESA_PLATEAU_MOUNTAINS));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_BRYCE), GlowBiome.getId(MESA_BRYCE));
    }
}
