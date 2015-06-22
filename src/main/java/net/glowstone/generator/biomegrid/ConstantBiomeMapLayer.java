package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;
import org.bukkit.block.Biome;

public class ConstantBiomeMapLayer extends MapLayer {

    private final Biome biome;

    public ConstantBiomeMapLayer(long seed, Biome biome) {
        super(seed);
        this.biome = biome;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                values[j + i * sizeX] = GlowBiome.getId(biome);
            }
        }
        return values;
    }
}
