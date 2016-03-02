package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import static org.bukkit.block.Biome.*;

public class BiomeMapLayer extends MapLayer {

    private static final int[] WARM = new int[]{GlowBiome.getId(DESERT), GlowBiome.getId(DESERT), GlowBiome.getId(DESERT),
            GlowBiome.getId(SAVANNA), GlowBiome.getId(SAVANNA), GlowBiome.getId(PLAINS)};
    private static final int[] WET = new int[]{GlowBiome.getId(PLAINS), GlowBiome.getId(PLAINS), GlowBiome.getId(FOREST),
            GlowBiome.getId(BIRCH_FOREST), GlowBiome.getId(ROOFED_FOREST), GlowBiome.getId(EXTREME_HILLS),
            GlowBiome.getId(SWAMPLAND)};
    private static final int[] DRY = new int[]{GlowBiome.getId(PLAINS), GlowBiome.getId(FOREST), GlowBiome.getId(TAIGA),
            GlowBiome.getId(EXTREME_HILLS)};
    private static final int[] WET_LARGE = new int[]{GlowBiome.getId(JUNGLE)};

    private final MapLayer belowLayer;

    public BiomeMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = belowLayer.generateValues(x, z, sizeX, sizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                int val = values[j + i * sizeX];
                if (val != 0) {
                    setCoordsSeed(x + j, z + i);
                    switch (val) {
                        case 1:
                            val = DRY[nextInt(DRY.length)];
                            break;
                        case 2:
                            val = WARM[nextInt(WARM.length)];
                            break;
                        case 3:
                        case 1004:
                            val = WET_LARGE[nextInt(WET_LARGE.length)];
                            break;
                        default:
                            break;
                    }
                }
                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }
}
