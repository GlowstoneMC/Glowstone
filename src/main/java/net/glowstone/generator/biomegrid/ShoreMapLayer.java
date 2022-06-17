package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bukkit.block.Biome.BADLANDS;
import static org.bukkit.block.Biome.BEACH;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.ERODED_BADLANDS;
import static org.bukkit.block.Biome.ICE_SPIKES;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.SNOWY_BEACH;
import static org.bukkit.block.Biome.SNOWY_TAIGA;
import static org.bukkit.block.Biome.SWAMP;

public class ShoreMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> SPECIAL_SHORES = new HashMap<>();

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        SPECIAL_SHORES.put(GlowBiome.getId(ICE_SPIKES), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_TAIGA), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SWAMP), GlowBiome.getId(SWAMP));
        SPECIAL_SHORES.put(GlowBiome.getId(BADLANDS), GlowBiome.getId(BADLANDS));
        SPECIAL_SHORES.put(GlowBiome.getId(ERODED_BADLANDS), GlowBiome.getId(ERODED_BADLANDS));
    }

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
                if (!OCEANS.contains(centerVal) && (
                        OCEANS.contains(upperVal) || OCEANS.contains(lowerVal)
                        || OCEANS.contains(leftVal) || OCEANS.contains(rightVal))) {
                    finalValues[j + i * sizeX] =
                            SPECIAL_SHORES.containsKey(centerVal)
                                    ? SPECIAL_SHORES.get(centerVal)
                                    : GlowBiome.getId(BEACH);
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }
}
