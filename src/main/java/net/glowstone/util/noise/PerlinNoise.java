package net.glowstone.util.noise;

import java.util.Random;
import org.bukkit.util.noise.PerlinNoiseGenerator;

public class PerlinNoise extends PerlinNoiseGenerator {

    /**
     * Creates an instance using the given PRNG.
     * @param rand the PRNG used to generate the seed permutation
     */
    public PerlinNoise(Random rand) {
        offsetX = rand.nextDouble() * 256;
        offsetY = rand.nextDouble() * 256;
        offsetZ = rand.nextDouble() * 256;
        // The only reason why I'm re-implementing the constructor code is that I've read
        // on at least 3 different sources that the permutation table should initially be
        // populated with indices.
        // "The permutation table is his answer to the issue of random numbers.
        // First take an array of decent length, usually 256 values. Fill it sequentially with each
        // number in that range: so index 1 gets 1, index 8 gets 8, index 251 gets 251, etc...
        // Then randomly shuffle the values so you have a table of 256 random values, but only
        // contains the values between 0 and 255."
        // source: https://code.google.com/p/fractalterraingeneration/wiki/Perlin_Noise
        for (int i = 0; i < 256; i++) {
            perm[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int pos = rand.nextInt(256 - i) + i;
            int old = perm[i];
            perm[i] = perm[pos];
            perm[pos] = old;
            perm[i + 256] = perm[i];
        }
    }

    public static int floor(double x) {
        int floored = (int) x;
        return x < floored ? floored - 1 : floored;
    }

    /**
     * Generates a rectangular section of this generator's noise.
     *
     * @param noise the output of the previous noise layer
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @param sizeX the size on the X axis
     * @param sizeY the size on the Y axis
     * @param sizeZ the size on the Z axis
     * @param scaleX the X scale parameter
     * @param scaleY the Y scale parameter
     * @param scaleZ the Z scale parameter
     * @param amplitude the amplitude parameter
     * @return {@code noise} with this layer of noise added
     */
    public double[] getNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY,
        int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        if (sizeY == 1) {
            return get2dNoise(noise, x, z, sizeX, sizeZ, scaleX, scaleZ, amplitude);
        } else {
            return get3dNoise(noise, x, y, z, sizeX, sizeY, sizeZ, scaleX, scaleY, scaleZ,
                amplitude);
        }
    }

    protected double[] get2dNoise(double[] noise, double x, double z, int sizeX, int sizeZ,
        double scaleX, double scaleZ, double amplitude) {
        int index = 0;
        for (int i = 0; i < sizeX; i++) {
            double dx = x + offsetX + i * scaleX;
            int floorX = floor(dx);
            int ix = floorX & 255;
            dx -= floorX;
            double fx = fade(dx);
            for (int j = 0; j < sizeZ; j++) {
                double dz = z + offsetZ + j * scaleZ;
                int floorZ = floor(dz);
                int iz = floorZ & 255;
                dz -= floorZ;
                double fz = fade(dz);
                // Hash coordinates of the square corners
                int a = perm[ix];
                int aa = perm[a] + iz;
                int b = perm[ix + 1];
                int ba = perm[b] + iz;
                double x1 = lerp(fx, grad(perm[aa], dx, 0, dz), grad(perm[ba], dx - 1, 0, dz));
                double x2 = lerp(fx, grad(perm[aa + 1], dx, 0, dz - 1),
                    grad(perm[ba + 1], dx - 1, 0, dz - 1));
                noise[index++] += lerp(fz, x1, x2) * amplitude;
            }
        }
        return noise;
    }

    protected double[] get3dNoise(double[] noise, double x, double y, double z, int sizeX,
        int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        int n = -1;
        double x1 = 0;
        double x2 = 0;
        double x3 = 0;
        double x4 = 0;
        int index = 0;
        for (int i = 0; i < sizeX; i++) {
            double dx = x + offsetX + i * scaleX;
            int floorX = floor(dx);
            int ix = floorX & 255;
            dx -= floorX;
            double fx = fade(dx);
            for (int j = 0; j < sizeZ; j++) {
                double dz = z + offsetZ + j * scaleZ;
                int floorZ = floor(dz);
                int iz = floorZ & 255;
                dz -= floorZ;
                double fz = fade(dz);
                for (int k = 0; k < sizeY; k++) {
                    double dy = y + offsetY + k * scaleY;
                    int floorY = floor(dy);
                    int iy = floorY & 255;
                    dy -= floorY;
                    double fy = fade(dy);
                    if (k == 0 || iy != n) {
                        n = iy;
                        // Hash coordinates of the cube corners
                        int a = perm[ix] + iy;
                        int aa = perm[a] + iz;
                        int ab = perm[a + 1] + iz;
                        int b = perm[ix + 1] + iy;
                        int ba = perm[b] + iz;
                        int bb = perm[b + 1] + iz;
                        x1 = lerp(fx, grad(perm[aa], dx, dy, dz), grad(perm[ba], dx - 1, dy, dz));
                        x2 = lerp(fx, grad(perm[ab], dx, dy - 1, dz),
                            grad(perm[bb], dx - 1, dy - 1, dz));
                        x3 = lerp(fx, grad(perm[aa + 1], dx, dy, dz - 1),
                            grad(perm[ba + 1], dx - 1, dy, dz - 1));
                        x4 = lerp(fx, grad(perm[ab + 1], dx, dy - 1, dz - 1),
                            grad(perm[bb + 1], dx - 1, dy - 1, dz - 1));
                    }
                    double y1 = lerp(fy, x1, x2);
                    double y2 = lerp(fy, x3, x4);

                    noise[index++] += lerp(fz, y1, y2) * amplitude;
                }
            }
        }
        return noise;
    }
}
