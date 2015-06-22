package net.glowstone.util.noise;

import org.bukkit.util.noise.NoiseGenerator;

import java.util.Random;

public class SimplexOctaveGenerator extends PerlinOctaveGenerator {

    public SimplexOctaveGenerator(Random rand, int octaves, int xSize, int zSize) {
        this(rand, octaves, xSize, 1, zSize);
    }

    public SimplexOctaveGenerator(Random rand, int octaves, int xSize, int ySize, int zSize) {
        super(createOctaves(rand, octaves), rand, xSize, ySize, zSize);
    }

    public SimplexOctaveGenerator(Random rand, int octaves) {
        this(rand, octaves, 0, 0, 0);
    }

    @Override
    public double[] fBm(double x, double y, double z, double lacunarity, double persistence) {
        for (int i = 0; i < noise.length; i++) {
            noise[i] = 0;
        }

        double freq = 1;
        double amp = 1;

        // fBm
        for (NoiseGenerator octave : octaves) {
            noise = ((SimplexNoise) octave).getNoise(noise, x, y, z, xSize, ySize, zSize, xScale * freq, yScale * freq, zScale * freq, 0.55D / amp);
            freq *= lacunarity;
            amp *= persistence;
        }

        return noise;
    }

    protected static NoiseGenerator[] createOctaves(Random rand, int octaves) {
        NoiseGenerator[] result = new NoiseGenerator[octaves];

        for (int i = 0; i < octaves; i++) {
            result[i] = new SimplexNoise(rand);
        }

        return result;
    }
}
