package net.glowstone.generator.biomegrid;

import java.util.HashMap;
import java.util.Map;

public class WhittakerMapLayer extends MapLayer {

    private static final Map<ClimateType, Climate> MAP = new HashMap<>();

    static {
        MAP.put(ClimateType.WARM_WET, new Climate(2, new int[]{3, 1}, 4));
        MAP.put(ClimateType.COLD_DRY, new Climate(3, new int[]{2, 4}, 1));
    }

    private final MapLayer belowLayer;
    private final ClimateType type;

    /**
     * Creates a map layer. TODO: improve documentation
     *
     * @param seed the layer random seed
     * @param belowLayer the layer generated before this one
     * @param type the climate-type parameter
     */
    public WhittakerMapLayer(long seed, MapLayer belowLayer, ClimateType type) {
        super(seed);
        this.belowLayer = belowLayer;
        this.type = type;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        if (type == ClimateType.WARM_WET || type == ClimateType.COLD_DRY) {
            return swapValues(x, z, sizeX, sizeZ);
        } else {
            return modifyValues(x, z, sizeX, sizeZ);
        }
    }

    private int[] swapValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        Climate climate = MAP.get(type);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (centerVal == climate.value) {
                    int upperVal = values[j + 1 + i * gridSizeX];
                    int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                    int leftVal = values[j + (i + 1) * gridSizeX];
                    int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                    for (int type : climate.crossTypes) {
                        if (upperVal == type || lowerVal == type || leftVal == type
                            || rightVal == type) {
                            centerVal = climate.finalValue;
                            break;
                        }
                    }
                }
                finalValues[j + i * sizeX] = centerVal;
            }
        }
        return finalValues;
    }

    private int[] modifyValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                int val = values[j + i * sizeX];
                if (val != 0) {
                    setCoordsSeed(x + j, z + i);
                    if (nextInt(13) == 0) {
                        val += 1000;
                    }
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }

    public enum ClimateType {
        WARM_WET,
        COLD_DRY,
        LARGER_BIOMES
    }

    private static class Climate {

        public final int value;
        public final int[] crossTypes;
        public final int finalValue;

        public Climate(int value, int[] crossTypes, int finalValue) {
            this.value = value;
            this.crossTypes = crossTypes;
            this.finalValue = finalValue;
        }
    }
}
