package net.glowstone.generator;

import net.glowstone.GlowWorld;
import net.glowstone.constants.GlowBiome;
import net.glowstone.generator.ground.*;
import net.glowstone.generator.ground.MesaGroundGenerator.MesaType;
import net.glowstone.generator.populators.*;
import net.glowstone.generator.populators.overworld.*;
import net.glowstone.util.noise.PerlinOctaveGenerator;
import net.glowstone.util.noise.SimplexOctaveGenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.OctaveGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.block.Biome.*;

public class OverworldGenerator extends GlowChunkGenerator {

    // Still need to parse ServerConfig.Key.GENERATOR_SETTINGS string
    // and set below fields from that.
    private static final double COORDINATE_SCALE = 684.412D;   // coordinateScale
    private static final double HEIGHT_SCALE = 684.412D;       // heightScale
    private static final double HEIGHT_NOISE_SCALE_X = 200.0D; // depthNoiseScaleX
    private static final double HEIGHT_NOISE_SCALE_Z = 200.0D; // depthNoiseScaleZ
    private static final double DETAIL_NOISE_SCALE_X = 80.0D;  // mainNoiseScaleX
    private static final double DETAIL_NOISE_SCALE_Y = 160.0D; // mainNoiseScaleY
    private static final double DETAIL_NOISE_SCALE_Z = 80.0D;  // mainNoiseScaleZ
    private static final double SURFACE_SCALE = 1 / 16.0D;
    private static final double BASE_SIZE = 8.5D;              // baseSize
    private static final double STRETCH_Y = 12.0D;             // stretchY
    private static final double BIOME_HEIGHT_OFFSET = 0.0D;    // biomeDepthOffset
    private static final double BIOME_HEIGHT_WEIGHT = 1.0D;    // biomeDepthWeight
    private static final double BIOME_SCALE_OFFSET = 0.0D;     // biomeScaleOffset
    private static final double BIOME_SCALE_WEIGHT = 1.0D;     // biomeScaleWeight

    private static final double[][] ELEVATION_WEIGHT = new double[5][5];
    private static final Map<Biome, GroundGenerator> GROUND_MAP = new HashMap<>();
    private static final Map<Biome, BiomeHeight> HEIGHT_MAP = new HashMap<>();
    private final double[][][] density = new double[5][5][33];
    private final GroundGenerator groundGen = new GroundGenerator();
    private final BiomeHeight defaultHeight = BiomeHeight.DEFAULT;

    public OverworldGenerator() {
        super(new OverworldPopulator(),
              new StructurePopulator(),
              new SnowPopulator());
    }

    @Override
    public short[][] generateExtBlockSectionsWithData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        final short[][] buf = generateRawTerrain(world, chunkX, chunkZ);

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        final double[] surfaceNoise = ((SimplexOctaveGenerator) getWorldOctaves(world).get("surface")).fBm(cx, cz, 0.5D, 0.5D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (GROUND_MAP.containsKey(biomes.getBiome(x, z))) {
                    GROUND_MAP.get(biomes.getBiome(x, z)).generateTerrainColumn(buf, world, random, cx + x, cz + z, biomes.getBiome(x, z), surfaceNoise[x | (z << 4)]);
                } else {
                    groundGen.generateTerrainColumn(buf, world, random, cx + x, cz + z, biomes.getBiome(x, z), surfaceNoise[x | (z << 4)]);
                }
            }
        }
        return buf;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        final Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
        return block.getType() == Material.GRASS;
    }

    @Override
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
        final Random seed = new Random(world.getSeed());

        OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 5, 5);
        gen.setXScale(HEIGHT_NOISE_SCALE_X);
        gen.setZScale(HEIGHT_NOISE_SCALE_Z);
        octaves.put("height", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 33, 5);
        gen.setXScale(COORDINATE_SCALE);
        gen.setYScale(HEIGHT_SCALE);
        gen.setZScale(COORDINATE_SCALE);
        octaves.put("roughness", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 33, 5);
        gen.setXScale(COORDINATE_SCALE);
        gen.setYScale(HEIGHT_SCALE);
        gen.setZScale(COORDINATE_SCALE);
        octaves.put("roughness2", gen);

        gen = new PerlinOctaveGenerator(seed, 8, 5, 33, 5);
        gen.setXScale(COORDINATE_SCALE / DETAIL_NOISE_SCALE_X);
        gen.setYScale(HEIGHT_SCALE / DETAIL_NOISE_SCALE_Y);
        gen.setZScale(COORDINATE_SCALE / DETAIL_NOISE_SCALE_Z);
        octaves.put("detail", gen);

        gen = new SimplexOctaveGenerator(seed, 4, 16, 16);
        gen.setScale(SURFACE_SCALE);
        octaves.put("surface", gen);
    }

    @SuppressWarnings("deprecation")
    private static void set(short[][] buf, int x, int y, int z, Material id) {
        if (buf[y >> 4] == null) {
            buf[y >> 4] = new short[4096];
        }
        buf[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (short) (id.getId() << 4);
    }

    private short[][] generateRawTerrain(World world, int chunkX, int chunkZ) {
        generateTerrainDensity(world, chunkX, chunkZ);

        int seaLevel = world.getSeaLevel();

        final short[][] buf = new short[16][];

        // Terrain densities where sampled at a lower res (scaled 4x along vertical, 8x along horizontal)
        // so it's needed to re-scale it. Linear interpolation is used to fill in the gaps.
        for (int i = 0; i < 5 - 1; i++) {
            for (int j = 0; j < 5 - 1; j++) {
                for (int k = 0; k < 33 - 1; k++) {
                    // 2x2 grid
                    double d1 = density[i][j][k];
                    double d2 = density[i + 1][j][k];
                    double d3 = density[i][j + 1][k];
                    double d4 = density[i + 1][j + 1][k];
                    // 2x2 grid (row above)
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
                                // any density higher than 0 is ground, any density lower or equal to 0 is air
                                // (or water if under the sea level).
                                if (dens > 0) {
                                    set(buf, m + (i << 2), l + (k << 3), n + (j << 2), Material.STONE);
                                } else if (l + (k << 3) < seaLevel - 1) {
                                    set(buf, m + (i << 2), l + (k << 3), n + (j << 2), Material.STATIONARY_WATER);
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

        return buf;
    }

    private void generateTerrainDensity(World world, int x, int z) {

        final WorldType type = world.getWorldType();

        // Scaling chunk x and z coordinates (4x, see below)
        x <<= 2;
        z <<= 2;

        // Get biome grid data at lower res (scaled 4x, at this scale a chunk is 4x4 columns of the biome grid),
        // we are loosing biome detail but saving huge amount of computation.
        // We need 1 chunk (4 columns) + 1 column for later needed outer edges (1 column) and at least 2 columns
        // on each side to be able to cover every value.
        // 4 + 1 + 2 + 2 = 9 columns but the biomegrid generator needs a multiple of 2 so we ask 10 columns wide
        // to the biomegrid generator.
        // This gives a total of 81 biome grid columns to work with, and this includes the chunk neighborhood.
        final int[] biomeGrid = ((GlowWorld) world).getChunkManager().getBiomeGridAtLowerRes(x - 2, z - 2, 10, 10);

        final Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
        final double[] heightNoise = ((PerlinOctaveGenerator) octaves.get("height")).fBm(x, z, 0.5D, 2.0D);
        final double[] roughnessNoise = ((PerlinOctaveGenerator) octaves.get("roughness")).fBm(x, 0, z, 0.5D, 2.0D);
        final double[] roughnessNoise2 = ((PerlinOctaveGenerator) octaves.get("roughness2")).fBm(x, 0, z, 0.5D, 2.0D);
        final double[] detailNoise = ((PerlinOctaveGenerator) octaves.get("detail")).fBm(x, 0, z, 0.5D, 2.0D);

        int index = 0;
        int indexHeight = 0;

        // Sampling densities.
        // Ideally we would sample 512 (4x4x32) values but in reality we need 825 values (5x5x33).
        // This is because linear interpolation is done later to re-scale so we need right and
        // bottom edge values if we want it to be "seamless".
        // You can check this picture to have a visualization of how the biomegrid is traversed (2D plan):
        // http://i.imgur.com/s4whlZE.png
        // The big square grid represents our lower res biomegrid columns, and the very small square grid
        // represents the normal biome grid columns (at block level) and the reason why it's required to
        // re-scale it and do linear interpolation before densities can be used to generate raw terrain.
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                double avgHeightScale = 0;
                double avgHeightBase = 0;
                double totalWeight = 0;
                final Biome biome = GlowBiome.getBiome(biomeGrid[i + 2 + (j + 2) * 10]);
                final BiomeHeight biomeHeight = HEIGHT_MAP.containsKey(biome) ? HEIGHT_MAP.get(biome) : defaultHeight;
                // Sampling an average height base and scale by visiting the neighborhood
                // of the current biomegrid column.
                for (int m = 0; m < 5; m++) {
                    for (int n = 0; n < 5; n++) {
                        final Biome nearBiome = GlowBiome.getBiome(biomeGrid[i + m + (j + n) * 10]);
                        final BiomeHeight nearBiomeHeight = HEIGHT_MAP.containsKey(nearBiome) ? HEIGHT_MAP.get(nearBiome) : defaultHeight;
                        double heightBase = BIOME_HEIGHT_OFFSET + nearBiomeHeight.getHeight() * BIOME_HEIGHT_WEIGHT;
                        double heightScale = BIOME_SCALE_OFFSET + nearBiomeHeight.getScale() * BIOME_SCALE_WEIGHT;
                        if (type == WorldType.AMPLIFIED && heightBase > 0) {
                            heightBase = 1.0D + heightBase * 2.0D;
                            heightScale = 1.0D + heightScale * 4.0D;
                        }
                        double weight = ELEVATION_WEIGHT[m][n] / (heightBase + 2.0D);
                        if (nearBiomeHeight.getHeight() > biomeHeight.getHeight()) {
                            weight *= 0.5D;
                        }
                        avgHeightScale += heightScale * weight;
                        avgHeightBase += heightBase * weight;
                        totalWeight += weight;
                    }
                }
                avgHeightScale /= totalWeight;
                avgHeightBase /= totalWeight;
                avgHeightScale = avgHeightScale * 0.9D + 0.1D;
                avgHeightBase = (avgHeightBase * 4.0D - 1.0D) / 8.0D;

                double noiseH = heightNoise[indexHeight++] / 8000.0D;
                if (noiseH < 0) {
                    noiseH = Math.abs(noiseH) * 0.3D;
                }
                noiseH = noiseH * 3.0D - 2.0D;
                if (noiseH < 0) {
                    noiseH = Math.max(noiseH * 0.5D, -1) / 1.4D * 0.5D;
                } else {
                    noiseH = Math.min(noiseH, 1) / 8.0D;
                }

                noiseH = ((noiseH * 0.2D + avgHeightBase) * BASE_SIZE / 8.0D) * 4.0D + BASE_SIZE;
                for (int k = 0; k < 33; k++) {
                    // density should be lower and lower as we climb up, this gets a height value to
                    // substract from the noise.
                    double nH = (k - noiseH) * STRETCH_Y * 128.0D / 256.0D / avgHeightScale;
                    if (nH < 0.0D) {
                        nH *= 4.0D;
                    }
                    double noiseR = roughnessNoise[index] / 512.0D;
                    double noiseR2 = roughnessNoise2[index] / 512.0D;
                    double noiseD = (detailNoise[index] / 10.0D + 1.0D) / 2.0D;
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens -= nH;
                    index++;
                    if (k > 29) {
                      double lowering = (k - 29) / 3.0D;
                      // linear interpolation
                      dens = dens * (1.0D - lowering) + (-10.0D * lowering);
                    }
                    density[i][j][k] = dens;
                }
            }
        }
    }

    private static void setBiomeSpecificGround(GroundGenerator gen, Biome... biomes) {
        for (Biome biome : biomes) {
            GROUND_MAP.put(biome, gen);
        }
    }

    private static void setBiomeHeight(BiomeHeight height, Biome... biomes) {
        for (Biome biome : biomes) {
            HEIGHT_MAP.put(biome, height);
        }
    }

    private static class BiomeHeight {
        public static final BiomeHeight DEFAULT = new BiomeHeight(0.1D, 0.2D);
        public static final BiomeHeight FLAT_SHORE = new BiomeHeight(0.0D, 0.025D);
        public static final BiomeHeight HIGH_PLATEAU = new BiomeHeight(1.5D, 0.025D);
        public static final BiomeHeight FLATLANDS = new BiomeHeight(0.125D, 0.05D);
        public static final BiomeHeight SWAMPLAND = new BiomeHeight(-0.2D, 0.1D);
        public static final BiomeHeight MID_PLAINS = new BiomeHeight(0.2D, 0.2D);
        public static final BiomeHeight FLATLANDS_HILLS = new BiomeHeight(0.275D, 0.25D);
        public static final BiomeHeight SWAMPLAND_HILLS = new BiomeHeight(-0.1D, 0.3D);
        public static final BiomeHeight LOW_HILLS = new BiomeHeight(0.2D, 0.3D);
        public static final BiomeHeight HILLS = new BiomeHeight(0.45D, 0.3D);
        public static final BiomeHeight MID_HILLS2 = new BiomeHeight(0.1D, 0.4D);
        public static final BiomeHeight DEFAULT_HILLS = new BiomeHeight(0.2D, 0.4D);
        public static final BiomeHeight MID_HILLS = new BiomeHeight(0.3D, 0.4D);
        public static final BiomeHeight BIG_HILLS = new BiomeHeight(0.525D, 0.55D);
        public static final BiomeHeight BIG_HILLS2 = new BiomeHeight(0.55D, 0.5D);
        public static final BiomeHeight EXTREME_HILLS = new BiomeHeight(1.0D, 0.5D);
        public static final BiomeHeight ROCKY_SHORE = new BiomeHeight(0.1D, 0.8D);
        public static final BiomeHeight LOW_SPIKES = new BiomeHeight(0.4125D, 1.325D);
        public static final BiomeHeight HIGH_SPIKES = new BiomeHeight(1.1D, 1.3125D);
        public static final BiomeHeight RIVER = new BiomeHeight(-0.5D, 0.0D);
        public static final BiomeHeight OCEAN = new BiomeHeight(-1.0D, 0.1D);
        public static final BiomeHeight DEEP_OCEAN = new BiomeHeight(-1.8D, 0.1D);

        private final double height;
        private final double scale;

        public BiomeHeight(double height, double scale) {
            this.height = height;
            this.scale = scale;
        }

        public double getHeight() {
            return height;
        }

        public double getScale() {
            return scale;
        }
    }

    static {
        setBiomeSpecificGround(new SandyGroundGenerator(), BEACH, COLD_BEACH, DESERT, DESERT_HILLS, DESERT_MOUNTAINS);
        setBiomeSpecificGround(new RockyGroundGenerator(), STONE_BEACH);
        setBiomeSpecificGround(new SnowyGroundGenerator(), ICE_PLAINS_SPIKES);
        setBiomeSpecificGround(new MycelGroundGenerator(), MUSHROOM_ISLAND, MUSHROOM_SHORE);
        setBiomeSpecificGround(new StonePatchGroundGenerator(), EXTREME_HILLS);
        setBiomeSpecificGround(new GravelPatchGroundGenerator(), EXTREME_HILLS_MOUNTAINS, EXTREME_HILLS_PLUS_MOUNTAINS);
        setBiomeSpecificGround(new DirtAndStonePatchGroundGenerator(), SAVANNA_MOUNTAINS, SAVANNA_PLATEAU_MOUNTAINS);
        setBiomeSpecificGround(new DirtPatchGroundGenerator(), MEGA_TAIGA, MEGA_TAIGA_HILLS, MEGA_SPRUCE_TAIGA, MEGA_SPRUCE_TAIGA_HILLS);
        setBiomeSpecificGround(new MesaGroundGenerator(), MESA, MESA_PLATEAU, MESA_PLATEAU_MOUNTAINS);
        setBiomeSpecificGround(new MesaGroundGenerator(MesaType.BRYCE), MESA_BRYCE);
        setBiomeSpecificGround(new MesaGroundGenerator(MesaType.FOREST), MESA_PLATEAU_FOREST, MESA_PLATEAU_FOREST_MOUNTAINS);

        setBiomeHeight(BiomeHeight.OCEAN, OCEAN, FROZEN_OCEAN);
        setBiomeHeight(BiomeHeight.DEEP_OCEAN, DEEP_OCEAN);
        setBiomeHeight(BiomeHeight.RIVER, RIVER, FROZEN_RIVER);
        setBiomeHeight(BiomeHeight.FLAT_SHORE, BEACH, COLD_BEACH, MUSHROOM_SHORE);
        setBiomeHeight(BiomeHeight.ROCKY_SHORE, STONE_BEACH);
        setBiomeHeight(BiomeHeight.FLATLANDS, DESERT, ICE_PLAINS, SAVANNA);
        setBiomeHeight(BiomeHeight.EXTREME_HILLS, EXTREME_HILLS, EXTREME_HILLS_PLUS, EXTREME_HILLS_MOUNTAINS, EXTREME_HILLS_PLUS_MOUNTAINS);
        setBiomeHeight(BiomeHeight.MID_PLAINS, TAIGA, COLD_TAIGA, MEGA_TAIGA);
        setBiomeHeight(BiomeHeight.SWAMPLAND, SWAMPLAND);
        setBiomeHeight(BiomeHeight.LOW_HILLS, MUSHROOM_ISLAND);
        setBiomeHeight(BiomeHeight.HILLS, ICE_MOUNTAINS, DESERT_HILLS, FOREST_HILLS, TAIGA_HILLS, SMALL_MOUNTAINS, JUNGLE_HILLS, BIRCH_FOREST_HILLS, COLD_TAIGA_HILLS, MEGA_TAIGA_HILLS, MESA_PLATEAU_FOREST_MOUNTAINS, MESA_PLATEAU_MOUNTAINS);
        setBiomeHeight(BiomeHeight.HIGH_PLATEAU, SAVANNA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU);
        setBiomeHeight(BiomeHeight.FLATLANDS_HILLS, DESERT_MOUNTAINS);
        setBiomeHeight(BiomeHeight.BIG_HILLS, ICE_PLAINS_SPIKES);
        setBiomeHeight(BiomeHeight.BIG_HILLS2, BIRCH_FOREST_HILLS_MOUNTAINS);
        setBiomeHeight(BiomeHeight.SWAMPLAND_HILLS, SWAMPLAND_MOUNTAINS);
        setBiomeHeight(BiomeHeight.DEFAULT_HILLS, JUNGLE_MOUNTAINS, JUNGLE_EDGE_MOUNTAINS, BIRCH_FOREST_MOUNTAINS, ROOFED_FOREST_MOUNTAINS);
        setBiomeHeight(BiomeHeight.MID_HILLS, TAIGA_MOUNTAINS, COLD_TAIGA_MOUNTAINS, MEGA_SPRUCE_TAIGA, MEGA_SPRUCE_TAIGA_HILLS);
        setBiomeHeight(BiomeHeight.MID_HILLS2, FLOWER_FOREST);
        setBiomeHeight(BiomeHeight.LOW_SPIKES, SAVANNA_MOUNTAINS);
        setBiomeHeight(BiomeHeight.HIGH_SPIKES, SAVANNA_PLATEAU_MOUNTAINS);

        // fill a 5x5 array with values that acts as elevation weight on chunk neighboring,
        // this can be viewed as a parabolic field: the center gets the more weight, and the
        // weight decreases as distance increases from the center. This is applied on the
        // lower scale biome grid.
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                int sqX = x - 2;
                sqX *= sqX;
                int sqZ = z - 2;
                sqZ *= sqZ;
                ELEVATION_WEIGHT[x][z] = 10.0D / Math.sqrt(sqX + sqZ + 0.2D);
            }
        }
    }
}
