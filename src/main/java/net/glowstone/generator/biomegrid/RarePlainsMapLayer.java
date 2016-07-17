package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.block.Biome.MUTATED_PLAINS;
import static org.bukkit.block.Biome.PLAINS;

public class RarePlainsMapLayer extends MapLayer {

    private static final Map<Integer, Integer> RARE_PLAINS = new HashMap<>();

    static {
        RARE_PLAINS.put(GlowBiome.getId(PLAINS), GlowBiome.getId(MUTATED_PLAINS));
    }

    private final MapLayer belowLayer;

    public RarePlainsMapLayer(long seed, MapLayer belowLayer) {
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
                setCoordsSeed(x + j, z + i);
                int centerValue = values[j + 1 + (i + 1) * gridSizeX];
                if (nextInt(57) == 0 && RARE_PLAINS.containsKey(centerValue)) {
                    centerValue = RARE_PLAINS.get(centerValue);
                }
                finalValues[j + i * sizeX] = centerValue;
            }
        }
        return finalValues;
    }
}
