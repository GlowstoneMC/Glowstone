package net.glowstone.generator.biomegrid;

import org.bukkit.util.noise.SimplexOctaveGenerator;

public class NoiseMapLayer extends MapLayer {

    private final SimplexOctaveGenerator noiseGen;

    public NoiseMapLayer(long seed) {
        super(seed);
        noiseGen = new SimplexOctaveGenerator(seed, 2);
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int[] values = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                double noise = noiseGen.noise(x + j, z + i, 0.175D, 0.8D, true) * 4.0D;
                int val = 0;
                if (noise >= 0.05D) {
                    val = noise <= 0.2D ? 3 : 2;
                } else {
                    setCoordsSeed(x + j, z + i);
                    val = nextInt(2) == 0 ? 3 : 0;
                }
                values[j + i * sizeX] = val;
                //values[j + i * sizeX] =
                //        noise >= -0.5D
                //                ? (double) noise >= 0.57D
                //                        ? 2
                //                : noise <= 0.2D
                //                        ? 3
                //                        : 2
                //        : nextInt(2) == 0
                //                        ? 3
                //                        : 0;
            }
        }
        return values;
    }
}
