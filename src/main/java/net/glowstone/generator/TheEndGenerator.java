package net.glowstone.generator;

import net.glowstone.GlowServer;
import net.glowstone.generator.populators.TheEndPopulator;
import net.glowstone.util.config.WorldConfig;
import net.glowstone.util.noise.PerlinOctaveGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.OctaveGenerator;

import java.util.Map;
import java.util.Random;

public class TheEndGenerator extends GlowChunkGenerator {

    private static double coordinateScale;
    private static double heightScale;
    private static double detailNoiseScaleX;  // mainNoiseScaleX
    private static double detailNoiseScaleY; // mainNoiseScaleY
    private static double detailNoiseScaleZ;  // mainNoiseScaleZ

    private final double[][][] density = new double[3][3][33];

    public TheEndGenerator() {
        super(new TheEndPopulator());

        WorldConfig config = GlowServer.getWorldConfig();

        coordinateScale = config.getDouble(WorldConfig.Key.END_COORDINATE_SCALE);
        heightScale = config.getDouble(WorldConfig.Key.END_HEIGHT_SCALE);
        detailNoiseScaleX = config.getDouble(WorldConfig.Key.END_DETAIL_NOISE_SCALE_X);
        detailNoiseScaleY = config.getDouble(WorldConfig.Key.END_DETAIL_NOISE_SCALE_Y);
        detailNoiseScaleZ = config.getDouble(WorldConfig.Key.END_DETAIL_NOISE_SCALE_Z);
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        return generateRawTerrain(world, chunkX, chunkZ);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
        return block.getType() == Material.ENDER_STONE;
    }

    @Override
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
        Random seed = new Random(world.getSeed());

        OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 3, 33, 3);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 3, 33, 3);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness2", gen);

        gen = new PerlinOctaveGenerator(seed, 8, 3, 33, 3);
        gen.setXScale(coordinateScale / detailNoiseScaleX);
        gen.setYScale(heightScale / detailNoiseScaleY);
        gen.setZScale(coordinateScale / detailNoiseScaleZ);
        octaves.put("detail", gen);
    }

    private ChunkData generateRawTerrain(World world, int chunkX, int chunkZ) {
        generateTerrainDensity(world, chunkX << 1, chunkZ << 1);

        ChunkData chunkData = createChunkData(world);

        for (int i = 0; i < 3 - 1; i++) {
            for (int j = 0; j < 3 - 1; j++) {
                for (int k = 0; k < 33 - 1; k++) {
                    double d1 = density[i][j][k];
                    double d2 = density[i + 1][j][k];
                    double d3 = density[i][j + 1][k];
                    double d4 = density[i + 1][j + 1][k];
                    double d5 = (density[i][j][k + 1] - d1) / 4;
                    double d6 = (density[i + 1][j][k + 1] - d2) / 4;
                    double d7 = (density[i][j + 1][k + 1] - d3) / 4;
                    double d8 = (density[i + 1][j + 1][k + 1] - d4) / 4;

                    for (int l = 0; l < 4; l++) {
                        double d9 = d1;
                        double d10 = d3;
                        for (int m = 0; m < 8; m++) {
                            double dens = d9;
                            for (int n = 0; n < 8; n++) {
                                // any density higher than 0 is ground, any density lower or equal to 0 is air.
                                if (dens > 0) {
                                    chunkData.setBlock(m + (i << 3), l + (k << 2), n + (j << 3), Material.ENDER_STONE);
                                }
                                // interpolation along z
                                dens += (d10 - d9) / 8;
                            }
                            // interpolation along x
                            d9 += (d2 - d1) / 8;
                            // interpolate along z
                            d10 += (d4 - d3) / 8;
                        }
                        // interpolation along y
                        d1 += d5;
                        d3 += d7;
                        d2 += d6;
                        d4 += d8;
                    }
                }
            }
        }

        return chunkData;
    }

    private void generateTerrainDensity(World world, int x, int z) {
        Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
        double[] roughnessNoise = ((PerlinOctaveGenerator) octaves.get("roughness")).fBm(x, 0, z, 0.5D, 2.0D);
        double[] roughnessNoise2 = ((PerlinOctaveGenerator) octaves.get("roughness2")).fBm(x, 0, z, 0.5D, 2.0D);
        double[] detailNoise = ((PerlinOctaveGenerator) octaves.get("detail")).fBm(x, 0, z, 0.5D, 2.0D);

        int index = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double nH = 100.0D - Math.sqrt((x + i) * (x + i) + (z + j) * (z + j)) * 8.0D;
                nH = Math.max(-100.0D, Math.min(80.0D, nH));
                for (int k = 0; k < 33; k++) {
                    double noiseR = roughnessNoise[index] / 512.0D;
                    double noiseR2 = roughnessNoise2[index] / 512.0D;
                    double noiseD = (detailNoise[index] / 10.0D + 1.0D) / 2.0D;
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens = dens - 8.0D + nH;
                    index++;
                    if (k < 8) {
                        double lowering = (8 - k) / 7;
                        dens = dens * (1.0D - lowering) + lowering * -30.0D;
                    } else if (k > 33 / 2 - 2) {
                        double lowering = (k - (33 / 2 - 2)) / 64.0D;
                        lowering = Math.max(0.0D, Math.min(1.0D, lowering));
                        dens = dens * (1.0D - lowering) + lowering * -3000.0D;
                    }
                    density[i][j][k] = dens;
                }
            }
        }
    }
}
