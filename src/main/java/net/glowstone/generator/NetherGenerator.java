package net.glowstone.generator;

import java.util.Map;
import java.util.Random;
import net.glowstone.GlowServer;
import net.glowstone.generator.populators.NetherPopulator;
import net.glowstone.util.config.WorldConfig;
import net.glowstone.util.noise.PerlinOctaveGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.OctaveGenerator;

public class NetherGenerator extends GlowChunkGenerator {

    private static double coordinateScale;
    private static double heightScale;
    private static double heightNoiseScaleX; // depthNoiseScaleX
    private static double heightNoiseScaleZ; // depthNoiseScaleZ
    private static double detailNoiseScaleX;  // mainNoiseScaleX
    private static double detailNoiseScaleY;  // mainNoiseScaleY
    private static double detailNoiseScaleZ;  // mainNoiseScaleZ
    private static double surfaceScale;

    private final double[][][] density = new double[5][5][17];

    /**
     * Creates a chunk generator for the Nether.
     */
    public NetherGenerator() {
        super(new NetherPopulator());

        WorldConfig config = GlowServer.getWorldConfig();

        coordinateScale = config.getDouble(WorldConfig.Key.NETHER_COORDINATE_SCALE);
        heightScale = config.getDouble(WorldConfig.Key.NETHER_HEIGHT_SCALE);
        heightNoiseScaleX = config.getDouble(WorldConfig.Key.NETHER_HEIGHT_NOISE_SCALE_X);
        heightNoiseScaleZ = config.getDouble(WorldConfig.Key.NETHER_HEIGHT_NOISE_SCALE_Z);
        detailNoiseScaleX = config.getDouble(WorldConfig.Key.NETHER_DETAIL_NOISE_SCALE_X);
        detailNoiseScaleY = config.getDouble(WorldConfig.Key.NETHER_DETAIL_NOISE_SCALE_Y);
        detailNoiseScaleZ = config.getDouble(WorldConfig.Key.NETHER_DETAIL_NOISE_SCALE_Z);
        surfaceScale = config.getDouble(WorldConfig.Key.NETHER_SURFACE_SCALE);
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ,
            BiomeGrid biomes) {
        ChunkData chunkData = generateRawTerrain(world, chunkX, chunkZ);

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        double[] surfaceNoise = ((PerlinOctaveGenerator) getWorldOctaves(world).get("surface"))
                .getFractalBrownianMotion(cx, cz, 0, 0.5D, 2.0D);
        double[] soulsandNoise = ((PerlinOctaveGenerator) getWorldOctaves(world).get("soulsand"))
                .getFractalBrownianMotion(cx, cz, 0, 0.5D, 2.0D);
        double[] gravelNoise = ((PerlinOctaveGenerator) getWorldOctaves(world).get("gravel"))
                .getFractalBrownianMotion(cx, 0, cz, 0.5D, 2.0D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                generateTerrainColumn(chunkData, world, random, cx + x, cz + z,
                        surfaceNoise[x | z << 4], soulsandNoise[x | z << 4], gravelNoise[x
                                | z << 4]);
            }
        }

        return chunkData;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
        return block.getType() == Material.NETHERRACK;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        while (true) {
            int x = random.nextInt(128) - 64;
            int y = 128 * 3 / 4;
            int z = random.nextInt(128) - 64;

            if (world.getBlockAt(x, y, z).isEmpty()) {
                while (world.getBlockAt(x, y - 1, z).isEmpty() && y > 0) {
                    y--;
                }
                return new Location(world, x, y, z);
            }
        }
    }

    @Override
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
        Random seed = new Random(world.getSeed());

        OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 5, 5);
        gen.setXScale(heightNoiseScaleX);
        gen.setZScale(heightNoiseScaleZ);
        octaves.put("height", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 17, 5);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 17, 5);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness2", gen);

        gen = new PerlinOctaveGenerator(seed, 8, 5, 17, 5);
        gen.setXScale(coordinateScale / detailNoiseScaleX);
        gen.setYScale(heightScale / detailNoiseScaleY);
        gen.setZScale(coordinateScale / detailNoiseScaleZ);
        octaves.put("detail", gen);

        gen = new PerlinOctaveGenerator(seed, 4, 16, 16, 1);
        gen.setScale(surfaceScale);
        octaves.put("surface", gen);

        gen = new PerlinOctaveGenerator(seed, 4, 16, 16, 1);
        gen.setXScale(surfaceScale / 2.0);
        gen.setYScale(surfaceScale / 2.0);
        octaves.put("soulsand", gen);

        gen = new PerlinOctaveGenerator(seed, 4, 16, 1, 16);
        gen.setXScale(surfaceScale / 2.0);
        gen.setZScale(surfaceScale / 2.0);
        octaves.put("gravel", gen);
    }

    private ChunkData generateRawTerrain(World world, int chunkX, int chunkZ) {
        generateTerrainDensity(world, chunkX << 2, chunkZ << 2);

        ChunkData chunkData = createChunkData(world);

        for (int i = 0; i < 5 - 1; i++) {
            for (int j = 0; j < 5 - 1; j++) {
                for (int k = 0; k < 17 - 1; k++) {
                    double d1 = density[i][j][k];
                    double d2 = density[i + 1][j][k];
                    double d3 = density[i][j + 1][k];
                    double d4 = density[i + 1][j + 1][k];
                    double d5 = (density[i][j][k + 1] - d1) / 8;
                    double d6 = (density[i + 1][j][k + 1] - d2) / 8;
                    double d7 = (density[i][j + 1][k + 1] - d3) / 8;
                    double d8 = (density[i + 1][j + 1][k + 1] - d4) / 8;

                    for (int l = 0; l < 8; l++) {
                        double d9 = d1;
                        double d10 = d3;
                        for (int m = 0; m < 4; m++) {
                            double dens = d9;
                            for (int n = 0; n < 4; n++) {
                                // any density higher than 0 is ground, any density lower or equal
                                // to 0 is air (or lava if under the lava level).
                                if (dens > 0) {
                                    chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                            Material.NETHERRACK);
                                } else if (l + (k << 3) < 32) {
                                    chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                            // TODO: 1.13 stationary lava
                                            Material.LAVA);
                                }
                                // interpolation along z
                                dens += (d10 - d9) / 4;
                            }
                            // interpolation along x
                            d9 += (d2 - d1) / 4;
                            // interpolate along z
                            d10 += (d4 - d3) / 4;
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
        double[] heightNoise = ((PerlinOctaveGenerator) octaves.get("height"))
                .getFractalBrownianMotion(x, z, 0.5D, 2.0D);
        double[] roughnessNoise = ((PerlinOctaveGenerator) octaves.get("roughness"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);
        double[] roughnessNoise2 = ((PerlinOctaveGenerator) octaves.get("roughness2"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);
        double[] detailNoise = ((PerlinOctaveGenerator) octaves.get("detail"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);

        double[] nv = new double[17];
        for (int i = 0; i < 17; i++) {
            nv[i] = Math.cos(i * Math.PI * 6.0D / 17.0D) * 2.0D;
            double nh = i > 17 / 2 ? 17 - 1 - i : i;
            if (nh < 4.0D) {
                nh = 4.0D - nh;
                nv[i] -= nh * nh * nh * 10.0D;
            }
        }

        int index = 0;
        int indexHeight = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                double noiseH = heightNoise[indexHeight++] / 8000.0D;
                if (noiseH < 0) {
                    noiseH = Math.abs(noiseH);
                }
                noiseH = noiseH * 3.0D - 3.0D;
                if (noiseH < 0) {
                    noiseH = Math.max(noiseH * 0.5D, -1) / 1.4D * 0.5D;
                } else {
                    noiseH = Math.min(noiseH, 1) / 6.0D;
                }

                noiseH = noiseH * 17 / 16.0D;
                for (int k = 0; k < 17; k++) {
                    double noiseR = roughnessNoise[index] / 512.0D;
                    double noiseR2 = roughnessNoise2[index] / 512.0D;
                    double noiseD = (detailNoise[index] / 10.0D + 1.0D) / 2.0D;
                    double nh = nv[k];
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR
                            : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens -= nh;
                    index++;
                    if (k > 13) {
                        double lowering = (k - 13) / 3.0D;
                        dens = dens * (1.0D - lowering) + lowering * -10.0D;
                    }
                    density[i][j][k] = dens;
                }
            }
        }
    }

    /**
     * Generates a terrain column.
     *
     * @param chunkData the chunk in which to generate
     * @param world the world
     * @param random the PRNG
     * @param x the column x coordinate
     * @param z the column z coordinate
     * @param surfaceNoise amplitude of surface-height variation
     * @param soulsandNoise determines the chance of a soul sand patch
     * @param gravelNoise determines the chance of a gravel patch
     */
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
            double surfaceNoise, double soulsandNoise, double gravelNoise) {
        Material topMat = Material.NETHERRACK;
        Material groundMat = Material.NETHERRACK;

        x = x & 0xF;
        z = z & 0xF;

        boolean soulSand = soulsandNoise + random.nextDouble() * 0.2D > 0;
        boolean gravel = gravelNoise + random.nextDouble() * 0.2D > 0;

        int surfaceHeight = (int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int deep = -1;
        for (int y = 127; y >= 0; y--) {
            if (y <= random.nextInt(5) || y >= 127 - random.nextInt(5)) {
                chunkData.setBlock(x, y, z, Material.BEDROCK);
                continue;
            }
            Material mat = chunkData.getType(x, y, z);
            if (mat == Material.AIR) {
                deep = -1;
            } else if (mat == Material.NETHERRACK) {
                if (deep == -1) {
                    if (surfaceHeight <= 0) {
                        topMat = Material.AIR;
                        groundMat = Material.NETHERRACK;
                    } else if (y >= 60 && y <= 65) {
                        topMat = Material.NETHERRACK;
                        groundMat = Material.NETHERRACK;
                        if (gravel) {
                            topMat = Material.GRAVEL;
                            groundMat = Material.NETHERRACK;
                        }
                        if (soulSand) {
                            topMat = Material.SOUL_SAND;
                            groundMat = Material.SOUL_SAND;
                        }
                    }

                    deep = surfaceHeight;
                    if (y >= 63) {
                        chunkData.setBlock(x, y, z, topMat);
                    } else {
                        chunkData.setBlock(x, y, z, groundMat);
                    }
                } else if (deep > 0) {
                    deep--;
                    chunkData.setBlock(x, y, z, groundMat);
                }
            }
        }
    }
}
