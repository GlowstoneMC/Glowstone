package net.glowstone.util.noise;

import com.jogamp.opencl.*;
import net.glowstone.util.OpenCL;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.nio.DoubleBuffer;
import java.util.Random;

public class PerlinNoise extends PerlinNoiseGenerator {

    public PerlinNoise(Random rand) {
        offsetX = rand.nextDouble() * 256;
        offsetY = rand.nextDouble() * 256;
        offsetZ = rand.nextDouble() * 256;
        // The only reason why I'm re-implementing the constructor code is that I've read
        // on at least 3 different sources that the permutation table should initially be
        // populated with indices.
        // "The permutation table is his answer to the issue of random numbers.
        // First take an array of decent length, usually 256 values. Fill it sequentially
        // with each number in that range: so index 1 gets 1, index 8 gets 8, index 251 gets 251, etc...
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
        int iX = (int) x;
        return x < iX ? iX - 1 : iX;
    }

    public double[] getNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        if (sizeY == 1) {
            return get2dNoise(noise, x, z, sizeX, sizeZ, scaleX, scaleZ, amplitude);
        } else {
            return get3dNoise(noise, x, y, z, sizeX, sizeY, sizeZ, scaleX, scaleY, scaleZ, amplitude);
        }
    }

    protected double[] get2dNoise(double[] noise, double x, double z, int sizeX, int sizeZ, double scaleX, double scaleZ, double amplitude) {
        int index = 0;
        for (int i = 0; i < sizeX; i++) {
            double dX = x + offsetX + i * scaleX;
            int floorX = floor(dX);
            int iX = floorX & 255;
            dX -= floorX;
            double fX = fade(dX);
            for (int j = 0; j < sizeZ; j++) {
                double dZ = z + offsetZ + j * scaleZ;
                int floorZ = floor(dZ);
                int iZ = floorZ & 255;
                dZ -= floorZ;
                double fZ = fade(dZ);
                // Hash coordinates of the square corners
                int a = perm[iX];
                int aa = perm[a] + iZ;
                int b = perm[iX + 1];
                int ba = perm[b] + iZ;
                double x1 = lerp(fX, grad(perm[aa], dX, 0, dZ), grad(perm[ba], dX - 1, 0, dZ));
                double x2 = lerp(fX, grad(perm[aa + 1], dX, 0, dZ - 1), grad(perm[ba + 1], dX - 1, 0, dZ - 1));
                noise[index++] += lerp(fZ, x1, x2) * amplitude;
            }
        }
        return noise;
    }

    protected double[] get3dNoise(double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double amplitude) {
        int n = -1;
        double x1 = 0;
        double x2 = 0;
        double x3 = 0;
        double x4 = 0;
        int index = 0;

        CLContext context = OpenCL.getContext();
        CLDevice device = OpenCL.getDevice();
        CLCommandQueue queue = OpenCL.getQueue();

        int localWorkSize = Math.min(device.getMaxWorkGroupSize(), 256);
        int globalSize = sizeX * sizeY * sizeZ;
        int r = globalSize % localWorkSize;
        if (r != 0) {
            globalSize += localWorkSize - r;
        }

        CLProgram program = OpenCL.getProgram("Lerp");

        CLBuffer<DoubleBuffer> xBuffer = context.createDoubleBuffer(globalSize, CLMemory.Mem.READ_ONLY);
        CLBuffer<DoubleBuffer> yBuffer = context.createDoubleBuffer(globalSize, CLMemory.Mem.READ_ONLY);
        CLBuffer<DoubleBuffer> zBuffer = context.createDoubleBuffer(globalSize, CLMemory.Mem.READ_ONLY);
        CLBuffer<DoubleBuffer> lerpBuffer = context.createDoubleBuffer(globalSize, CLMemory.Mem.WRITE_ONLY);

        for (int i = 0; i < sizeX; i++) {
            double dX = x + offsetX + i * scaleX;
            int floorX = floor(dX);
            int iX = floorX & 255;
            dX -= floorX;
            double fX = fade(dX);
            for (int j = 0; j < sizeZ; j++) {
                double dZ = z + offsetZ + j * scaleZ;
                int floorZ = floor(dZ);
                int iZ = floorZ & 255;
                dZ -= floorZ;
                double fZ = fade(dZ);
                for (int k = 0; k < sizeY; k++) {
                    double dY = y + offsetY + k * scaleY;
                    int floorY = floor(dY);
                    int iY = floorY & 255;
                    dY -= floorY;
                    double fY = fade(dY);
                    if (k == 0 || iY != n) {
                        n = iY;
                        // Hash coordinates of the cube corners
                        int a = perm[iX] + iY;
                        int aa = perm[a] + iZ;
                        int ab = perm[a + 1] + iZ;
                        int b = perm[iX + 1] + iY;
                        int ba = perm[b] + iZ;
                        int bb = perm[b + 1] + iZ;
                        x1 = lerp(fX, grad(perm[aa], dX, dY, dZ), grad(perm[ba], dX - 1, dY, dZ));
                        x2 = lerp(fX, grad(perm[ab], dX, dY - 1, dZ), grad(perm[bb], dX - 1, dY - 1, dZ));
                        x3 = lerp(fX, grad(perm[aa + 1], dX, dY, dZ - 1), grad(perm[ba + 1], dX - 1, dY, dZ - 1));
                        x4 = lerp(fX, grad(perm[ab + 1], dX, dY - 1, dZ - 1), grad(perm[bb + 1], dX - 1, dY - 1, dZ - 1));
                    }
                    double y1 = lerp(fY, x1, x2);
                    double y2 = lerp(fY, x3, x4);

                    xBuffer.getBuffer().put(fZ);
                    yBuffer.getBuffer().put(y1);
                    zBuffer.getBuffer().put(y2);
                }
            }
        }

        xBuffer.getBuffer().rewind();
        yBuffer.getBuffer().rewind();
        zBuffer.getBuffer().rewind();

        CLKernel kernel = program.createCLKernel("Lerp");
        kernel.putArgs(xBuffer, yBuffer, zBuffer, lerpBuffer).putArg(sizeX * sizeY * sizeZ).putArg(amplitude);
        queue.putWriteBuffer(xBuffer, false)
                .putWriteBuffer(yBuffer, false)
                .putWriteBuffer(zBuffer, false)
                .put1DRangeKernel(kernel, 0, globalSize, localWorkSize)
                .putReadBuffer(lerpBuffer, true);

        for (int i = 0; i < noise.length; i++) {
            noise[i] += lerpBuffer.getBuffer().get();
        }

        xBuffer.release();
        yBuffer.release();
        zBuffer.release();
        lerpBuffer.release();

        return noise;
    }
}
