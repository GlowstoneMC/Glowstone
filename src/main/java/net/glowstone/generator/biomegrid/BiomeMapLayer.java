package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import static org.bukkit.block.Biome.BIRCH_FOREST;
import static org.bukkit.block.Biome.DARK_FOREST;
import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.FOREST;
import static org.bukkit.block.Biome.JUNGLE;
import static org.bukkit.block.Biome.PLAINS;
import static org.bukkit.block.Biome.SAVANNA;
import static org.bukkit.block.Biome.SNOWY_TAIGA;
import static org.bukkit.block.Biome.SWAMP;
import static org.bukkit.block.Biome.TAIGA;

public class BiomeMapLayer extends MapLayer {

    private static final int[] WARM = new int[]{
            GlowBiome.getId(DESERT),
            GlowBiome.getId(DESERT),
            GlowBiome.getId(DESERT),
            GlowBiome.getId(SAVANNA),
            GlowBiome.getId(SAVANNA),
            GlowBiome.getId(PLAINS)};
    private static final int[] WET = new int[]{
            GlowBiome.getId(PLAINS),
            GlowBiome.getId(PLAINS),
            GlowBiome.getId(FOREST),
            GlowBiome.getId(BIRCH_FOREST),
            GlowBiome.getId(DARK_FOREST),
            GlowBiome.getId(SWAMP)};
    private static final int[] DRY = new int[]{
            GlowBiome.getId(PLAINS),
            GlowBiome.getId(FOREST),
            GlowBiome.getId(TAIGA)};
    private static final int[] COLD = new int[]{
            GlowBiome.getId(SNOWY_TAIGA)};
    private static final int[] WET_LARGE = new int[]{
            GlowBiome.getId(JUNGLE)};

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
                        case 1003:
                            val = COLD[nextInt(COLD.length)];
                            break;
                        case 4:
                            val = WET[nextInt(WET.length)];
                            break;
                        case 1001:
                            val = DRY[nextInt(DRY.length)];
                            break;
                        case 1002:
                            val = WARM[nextInt(WARM.length)];
                            break;
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
