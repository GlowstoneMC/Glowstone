package net.glowstone.generator;

import static net.glowstone.GlowServer.getWorldConfig;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_BIG_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_BIG_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEEP_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEFAULT;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEFAULT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_EXTREME_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLATLANDS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLATLANDS_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLAT_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HIGH_PLATEAU;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HIGH_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_LOW_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_LOW_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_PLAINS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_RIVER;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_ROCKY_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_SWAMPLAND;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_SWAMPLAND_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_BIG_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_BIG_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEEP_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEFAULT;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEFAULT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_EXTREME_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLATLANDS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLATLANDS_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLAT_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HIGH_PLATEAU;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HIGH_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_LOW_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_LOW_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_PLAINS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_RIVER;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_ROCKY_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_SWAMPLAND;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_SWAMPLAND_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_BASE_SIZE;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_BIOME_HEIGHT_OFFSET;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_BIOME_HEIGHT_WEIGHT;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_BIOME_SCALE_OFFSET;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_BIOME_SCALE_WEIGHT;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_COORDINATE_SCALE;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DENSITY_FILL_MODE;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DENSITY_FILL_OFFSET;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DENSITY_FILL_SEA_MODE;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DETAIL_NOISE_SCALE_X;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DETAIL_NOISE_SCALE_Y;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_DETAIL_NOISE_SCALE_Z;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_HEIGHT_NOISE_SCALE_X;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_HEIGHT_NOISE_SCALE_Z;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_HEIGHT_SCALE;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_STRETCH_Y;
import static net.glowstone.util.config.WorldConfig.Key.OVERWORLD_SURFACE_SCALE;
import static org.bukkit.block.Biome.BEACHES;
import static org.bukkit.block.Biome.BIRCH_FOREST_HILLS;
import static org.bukkit.block.Biome.COLD_BEACH;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.DESERT_HILLS;
import static org.bukkit.block.Biome.EXTREME_HILLS;
import static org.bukkit.block.Biome.EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.FOREST_HILLS;
import static org.bukkit.block.Biome.FROZEN_OCEAN;
import static org.bukkit.block.Biome.FROZEN_RIVER;
import static org.bukkit.block.Biome.ICE_FLATS;
import static org.bukkit.block.Biome.ICE_MOUNTAINS;
import static org.bukkit.block.Biome.JUNGLE_HILLS;
import static org.bukkit.block.Biome.MESA;
import static org.bukkit.block.Biome.MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MESA_ROCK;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND_SHORE;
import static org.bukkit.block.Biome.MUTATED_BIRCH_FOREST;
import static org.bukkit.block.Biome.MUTATED_BIRCH_FOREST_HILLS;
import static org.bukkit.block.Biome.MUTATED_DESERT;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.MUTATED_FOREST;
import static org.bukkit.block.Biome.MUTATED_ICE_FLATS;
import static org.bukkit.block.Biome.MUTATED_JUNGLE;
import static org.bukkit.block.Biome.MUTATED_JUNGLE_EDGE;
import static org.bukkit.block.Biome.MUTATED_MESA;
import static org.bukkit.block.Biome.MUTATED_MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MUTATED_MESA_ROCK;
import static org.bukkit.block.Biome.MUTATED_REDWOOD_TAIGA;
import static org.bukkit.block.Biome.MUTATED_REDWOOD_TAIGA_HILLS;
import static org.bukkit.block.Biome.MUTATED_ROOFED_FOREST;
import static org.bukkit.block.Biome.MUTATED_SAVANNA;
import static org.bukkit.block.Biome.MUTATED_SAVANNA_ROCK;
import static org.bukkit.block.Biome.MUTATED_SWAMPLAND;
import static org.bukkit.block.Biome.MUTATED_TAIGA;
import static org.bukkit.block.Biome.MUTATED_TAIGA_COLD;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.REDWOOD_TAIGA;
import static org.bukkit.block.Biome.REDWOOD_TAIGA_HILLS;
import static org.bukkit.block.Biome.RIVER;
import static org.bukkit.block.Biome.SAVANNA;
import static org.bukkit.block.Biome.SAVANNA_ROCK;
import static org.bukkit.block.Biome.SMALLER_EXTREME_HILLS;
import static org.bukkit.block.Biome.STONE_BEACH;
import static org.bukkit.block.Biome.SWAMPLAND;
import static org.bukkit.block.Biome.TAIGA;
import static org.bukkit.block.Biome.TAIGA_COLD;
import static org.bukkit.block.Biome.TAIGA_COLD_HILLS;
import static org.bukkit.block.Biome.TAIGA_HILLS;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLProgram;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.constants.GlowBiome;
import net.glowstone.generator.ground.DirtAndStonePatchGroundGenerator;
import net.glowstone.generator.ground.DirtPatchGroundGenerator;
import net.glowstone.generator.ground.GravelPatchGroundGenerator;
import net.glowstone.generator.ground.GroundGenerator;
import net.glowstone.generator.ground.MesaGroundGenerator;
import net.glowstone.generator.ground.MesaGroundGenerator.MesaType;
import net.glowstone.generator.ground.MycelGroundGenerator;
import net.glowstone.generator.ground.RockyGroundGenerator;
import net.glowstone.generator.ground.SandyGroundGenerator;
import net.glowstone.generator.ground.SnowyGroundGenerator;
import net.glowstone.generator.ground.StonePatchGroundGenerator;
import net.glowstone.generator.populators.OverworldPopulator;
import net.glowstone.generator.populators.StructurePopulator;
import net.glowstone.generator.populators.overworld.SnowPopulator;
import net.glowstone.util.OpenCompute;
import net.glowstone.util.noise.PerlinOctaveGenerator;
import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.OctaveGenerator;

public class OverworldGenerator extends GlowChunkGenerator {

    private static final double[][] ELEVATION_WEIGHT = new double[5][5];
    private static final Map<Biome, GroundGenerator> GROUND_MAP = new HashMap<>();
    private static final Map<Biome, BiomeHeight> HEIGHT_MAP = new HashMap<>();
    private static double coordinateScale;
    private static double heightScale;
    private static double heightNoiseScaleX; // depthNoiseScaleX
    private static double heightNoiseScaleZ; // depthNoiseScaleZ
    private static double detailNoiseScaleX;  // mainNoiseScaleX
    private static double detailNoiseScaleY; // mainNoiseScaleY
    private static double detailNoiseScaleZ;  // mainNoiseScaleZ
    private static double surfaceScale;
    private static double baseSize;
    private static double stretchY;
    private static double biomeHeightOffset;    // biomeDepthOffset
    private static double biomeHeightWeight;    // biomeDepthWeight
    private static double biomeScaleOffset;
    private static double biomeScaleWeight;

    static {
        setBiomeSpecificGround(new SandyGroundGenerator(), BEACHES, COLD_BEACH, DESERT,
                DESERT_HILLS, MUTATED_DESERT);
        setBiomeSpecificGround(new RockyGroundGenerator(), STONE_BEACH);
        setBiomeSpecificGround(new SnowyGroundGenerator(), MUTATED_ICE_FLATS);
        setBiomeSpecificGround(new MycelGroundGenerator(), MUSHROOM_ISLAND, MUSHROOM_ISLAND_SHORE);
        setBiomeSpecificGround(new StonePatchGroundGenerator(), EXTREME_HILLS);
        setBiomeSpecificGround(new GravelPatchGroundGenerator(), MUTATED_EXTREME_HILLS,
                MUTATED_EXTREME_HILLS_WITH_TREES);
        setBiomeSpecificGround(new DirtAndStonePatchGroundGenerator(), MUTATED_SAVANNA,
                MUTATED_SAVANNA_ROCK);
        setBiomeSpecificGround(new DirtPatchGroundGenerator(), REDWOOD_TAIGA, REDWOOD_TAIGA_HILLS,
                MUTATED_REDWOOD_TAIGA, MUTATED_REDWOOD_TAIGA_HILLS);
        setBiomeSpecificGround(new MesaGroundGenerator(), MESA, MESA_CLEAR_ROCK, MESA_ROCK);
        setBiomeSpecificGround(new MesaGroundGenerator(MesaType.BRYCE), MUTATED_MESA);
        setBiomeSpecificGround(new MesaGroundGenerator(MesaType.FOREST), MESA_ROCK,
                MUTATED_MESA_ROCK);

        setBiomeHeight(BiomeHeight.OCEAN, OCEAN, FROZEN_OCEAN);
        setBiomeHeight(BiomeHeight.DEEP_OCEAN, DEEP_OCEAN);
        setBiomeHeight(BiomeHeight.RIVER, RIVER, FROZEN_RIVER);
        setBiomeHeight(BiomeHeight.FLAT_SHORE, BEACHES, COLD_BEACH, MUSHROOM_ISLAND_SHORE);
        setBiomeHeight(BiomeHeight.ROCKY_SHORE, STONE_BEACH);
        setBiomeHeight(BiomeHeight.FLATLANDS, DESERT, ICE_FLATS, SAVANNA);
        setBiomeHeight(BiomeHeight.EXTREME_HILLS, EXTREME_HILLS, EXTREME_HILLS_WITH_TREES,
                MUTATED_EXTREME_HILLS, MUTATED_EXTREME_HILLS_WITH_TREES);
        setBiomeHeight(BiomeHeight.MID_PLAINS, TAIGA, TAIGA_COLD, REDWOOD_TAIGA);
        setBiomeHeight(BiomeHeight.SWAMPLAND, SWAMPLAND);
        setBiomeHeight(BiomeHeight.LOW_HILLS, MUSHROOM_ISLAND);
        setBiomeHeight(BiomeHeight.HILLS, ICE_MOUNTAINS, DESERT_HILLS, FOREST_HILLS, TAIGA_HILLS,
                SMALLER_EXTREME_HILLS, JUNGLE_HILLS, BIRCH_FOREST_HILLS, TAIGA_COLD_HILLS,
                REDWOOD_TAIGA_HILLS, MUTATED_MESA_ROCK, MUTATED_MESA_CLEAR_ROCK);
        setBiomeHeight(BiomeHeight.HIGH_PLATEAU, SAVANNA_ROCK, MESA_ROCK, MESA_CLEAR_ROCK);
        setBiomeHeight(BiomeHeight.FLATLANDS_HILLS, MUTATED_DESERT);
        setBiomeHeight(BiomeHeight.BIG_HILLS, MUTATED_ICE_FLATS);
        setBiomeHeight(BiomeHeight.BIG_HILLS2, MUTATED_BIRCH_FOREST_HILLS);
        setBiomeHeight(BiomeHeight.SWAMPLAND_HILLS, MUTATED_SWAMPLAND);
        setBiomeHeight(BiomeHeight.DEFAULT_HILLS, MUTATED_JUNGLE, MUTATED_JUNGLE_EDGE,
                MUTATED_BIRCH_FOREST, MUTATED_ROOFED_FOREST);
        setBiomeHeight(BiomeHeight.MID_HILLS, MUTATED_TAIGA, MUTATED_TAIGA_COLD,
                MUTATED_REDWOOD_TAIGA, MUTATED_REDWOOD_TAIGA_HILLS);
        setBiomeHeight(BiomeHeight.MID_HILLS2, MUTATED_FOREST);
        setBiomeHeight(BiomeHeight.LOW_SPIKES, MUTATED_SAVANNA);
        setBiomeHeight(BiomeHeight.HIGH_SPIKES, MUTATED_SAVANNA_ROCK);

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

    private final double[][][] density = new double[5][5][33];
    private final GroundGenerator groundGen = new GroundGenerator();
    private final BiomeHeight defaultHeight = BiomeHeight.DEFAULT;

    /**
     * Creates the generator for the overworld.
     */
    public OverworldGenerator() {
        super(new OverworldPopulator(),
                new StructurePopulator(),
                new SnowPopulator());

        coordinateScale = getWorldConfig().getDouble(OVERWORLD_COORDINATE_SCALE);
        heightScale = getWorldConfig().getDouble(OVERWORLD_HEIGHT_SCALE);
        heightNoiseScaleX = getWorldConfig().getDouble(OVERWORLD_HEIGHT_NOISE_SCALE_X);
        heightNoiseScaleZ = getWorldConfig().getDouble(OVERWORLD_HEIGHT_NOISE_SCALE_Z);
        detailNoiseScaleX = getWorldConfig().getDouble(OVERWORLD_DETAIL_NOISE_SCALE_X);
        detailNoiseScaleY = getWorldConfig().getDouble(OVERWORLD_DETAIL_NOISE_SCALE_Y);
        detailNoiseScaleZ = getWorldConfig().getDouble(OVERWORLD_DETAIL_NOISE_SCALE_Z);
        surfaceScale = getWorldConfig().getDouble(OVERWORLD_SURFACE_SCALE);
        baseSize = getWorldConfig().getDouble(OVERWORLD_BASE_SIZE);
        stretchY = getWorldConfig().getDouble(OVERWORLD_STRETCH_Y);
        biomeHeightOffset = getWorldConfig().getDouble(OVERWORLD_BIOME_HEIGHT_OFFSET);
        biomeHeightWeight = getWorldConfig().getDouble(OVERWORLD_BIOME_HEIGHT_WEIGHT);
        biomeScaleOffset = getWorldConfig().getDouble(OVERWORLD_BIOME_SCALE_OFFSET);
        biomeScaleWeight = getWorldConfig().getDouble(OVERWORLD_BIOME_SCALE_WEIGHT);
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

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ,
            BiomeGrid biomes) {
        ChunkData chunkData = generateRawTerrain(world, chunkX, chunkZ);

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        SimplexOctaveGenerator octaveGenerator = ((SimplexOctaveGenerator) getWorldOctaves(world)
                .get("surface"));
        int sizeX = octaveGenerator.getSizeX();
        int sizeZ = octaveGenerator.getSizeZ();
        if (((GlowServer) GlowServerProvider.getServer()).doesUseGraphicsCompute()) {
            CLKernel noiseGen = null;
            CLBuffer<FloatBuffer> noise = null;
            try {
                // Initialize OpenCL stuff and put args
                CLProgram program = OpenCompute.getProgram("net/glowstone/CLRandom.cl");
                int workSize = sizeX * octaveGenerator.getSizeY() * sizeZ;
                noise = OpenCompute.getContext()
                        .createFloatBuffer(workSize, CLMemory.Mem.WRITE_ONLY);
                noiseGen = OpenCompute.getKernel(program, "GenerateNoise");
                noiseGen.putArg(random.nextFloat())
                        .putArg(random.nextFloat())
                        .putArg(noise)
                        .putArg(workSize);

                // Calculate noise on GPU
                OpenCompute.getQueue()
                        .put1DRangeKernel(noiseGen, 0, OpenCompute.getGlobalSize(workSize),
                                OpenCompute.getLocalSize())
                        .putReadBuffer(noise, true);

                // Use noise
                for (int x = 0; x < sizeX; x++) {
                    for (int z = 0; z < sizeZ; z++) {
                        if (GROUND_MAP.containsKey(biomes.getBiome(x, z))) {
                            GROUND_MAP.get(biomes.getBiome(x, z))
                                    .generateTerrainColumn(chunkData, world, random, cx + x, cz + z,
                                            biomes.getBiome(x, z), noise.getBuffer()
                                                    .get(x | z << 4));
                        } else {
                            groundGen
                                    .generateTerrainColumn(chunkData, world, random, cx + x, cz + z,
                                            biomes.getBiome(x, z), noise.getBuffer()
                                                    .get(x | z << 4));
                        }
                    }
                }
            } finally {
                // Clean up
                if (noise != null) {
                    GlowServerProvider.getServer().getScheduler().runTaskAsynchronously(null, noise::release);
                }
                if (noiseGen != null) {
                    noiseGen.rewind();
                }
            }
        } else {
            double[] surfaceNoise = octaveGenerator.getFractalBrownianMotion(cx, cz, 0.5D, 0.5D);
            for (int x = 0; x < sizeX; x++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (GROUND_MAP.containsKey(biomes.getBiome(x, z))) {
                        GROUND_MAP.get(biomes.getBiome(x, z))
                                .generateTerrainColumn(chunkData, world, random, cx + x, cz + z,
                                        biomes.getBiome(x, z), surfaceNoise[x | z << 4]);
                    } else {
                        groundGen.generateTerrainColumn(chunkData, world, random, cx + x, cz + z,
                                biomes.getBiome(x, z), surfaceNoise[x | z << 4]);
                    }
                }
            }
        }
        return chunkData;
    }

    @Override
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
        Random seed = new Random(world.getSeed());

        OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 5, 5);
        gen.setXScale(heightNoiseScaleX);
        gen.setZScale(heightNoiseScaleZ);
        octaves.put("height", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 33, 5);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness", gen);

        gen = new PerlinOctaveGenerator(seed, 16, 5, 33, 5);
        gen.setXScale(coordinateScale);
        gen.setYScale(heightScale);
        gen.setZScale(coordinateScale);
        octaves.put("roughness2", gen);

        gen = new PerlinOctaveGenerator(seed, 8, 5, 33, 5);
        gen.setXScale(coordinateScale / detailNoiseScaleX);
        gen.setYScale(heightScale / detailNoiseScaleY);
        gen.setZScale(coordinateScale / detailNoiseScaleZ);
        octaves.put("detail", gen);

        gen = new SimplexOctaveGenerator(seed, 4, 16, 16);
        gen.setScale(surfaceScale);
        octaves.put("surface", gen);
    }

    private ChunkData generateRawTerrain(World world, int chunkX, int chunkZ) {
        generateTerrainDensity(world, chunkX, chunkZ);

        int seaLevel = world.getSeaLevel();

        ChunkData chunkData = createChunkData(world);

        // Terrain densities are sampled at different resolutions (1/4x on x,z and 1/8x on y by
        // default)
        // so it's needed to re-scale it. Linear interpolation is used to fill in the gaps.

        int fill = getWorldConfig().getInt(OVERWORLD_DENSITY_FILL_MODE);
        int afill = Math.abs(fill);
        int seaFill = getWorldConfig().getInt(OVERWORLD_DENSITY_FILL_SEA_MODE);
        double densityOffset = getWorldConfig().getDouble(OVERWORLD_DENSITY_FILL_OFFSET);

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
                                // any density higher than density offset is ground, any density
                                // lower or equal to the density offset is air
                                // (or water if under the sea level).
                                // this can be flipped if the mode is negative, so lower or equal
                                // to is ground, and higher is air/water
                                // and, then data can be shifted by afill the order is air by
                                // default, ground, then water. they can shift places
                                // within each if statement
                                // the target is densityOffset + 0, since the default target is
                                // 0, so don't get too confused by the naming :)
                                if (afill == 1 || afill == 10 || afill == 13 || afill == 16) {
                                    chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                            Material.STATIONARY_WATER);
                                } else if (afill == 2 || afill == 9 || afill == 12 || afill == 15) {
                                    chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                            Material.STONE);
                                }
                                if (dens > densityOffset && fill > -1
                                        || dens <= densityOffset && fill < 0) {
                                    if (afill == 0 || afill == 3 || afill == 6 || afill == 9
                                            || afill == 12) {
                                        chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                                Material.STONE);
                                    } else if (afill == 2 || afill == 7 || afill == 10
                                            || afill == 16) {
                                        chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                                Material.STATIONARY_WATER);
                                    }
                                } else if (l + (k << 3) < seaLevel - 1 && seaFill == 0
                                        || l + (k << 3) >= seaLevel - 1 && seaFill == 1) {
                                    if (afill == 0 || afill == 3 || afill == 7 || afill == 10
                                            || afill == 13) {
                                        chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                                Material.STATIONARY_WATER);
                                    } else if (afill == 1 || afill == 6 || afill == 9
                                            || afill == 15) {
                                        chunkData.setBlock(m + (i << 2), l + (k << 3), n + (j << 2),
                                                Material.STONE);
                                    }
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

        WorldType type = world.getWorldType();

        // Scaling chunk x and z coordinates (4x, see below)
        x <<= 2;
        z <<= 2;

        // Get biome grid data at lower res (scaled 4x, at this scale a chunk is 4x4 columns of
        // the biome grid),
        // we are loosing biome detail but saving huge amount of computation.
        // We need 1 chunk (4 columns) + 1 column for later needed outer edges (1 column) and at
        // least 2 columns
        // on each side to be able to cover every value.
        // 4 + 1 + 2 + 2 = 9 columns but the biomegrid generator needs a multiple of 2 so we ask
        // 10 columns wide
        // to the biomegrid generator.
        // This gives a total of 81 biome grid columns to work with, and this includes the chunk
        // neighborhood.
        int[] biomeGrid = ((GlowWorld) world).getChunkManager()
                .getBiomeGridAtLowerRes(x - 2, z - 2, 10, 10);

        Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
        double[] heightNoise = ((PerlinOctaveGenerator) octaves.get("height"))
                .getFractalBrownianMotion(x, z, 0.5D, 2.0D);
        double[] roughnessNoise = ((PerlinOctaveGenerator) octaves.get("roughness"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);
        double[] roughnessNoise2 = ((PerlinOctaveGenerator) octaves.get("roughness2"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);
        double[] detailNoise = ((PerlinOctaveGenerator) octaves.get("detail"))
                .getFractalBrownianMotion(x, 0, z, 0.5D, 2.0D);

        int index = 0;
        int indexHeight = 0;

        // Sampling densities.
        // Ideally we would sample 512 (4x4x32) values but in reality we need 825 values (5x5x33).
        // This is because linear interpolation is done later to re-scale so we need right and
        // bottom edge values if we want it to be "seamless".
        // You can check this picture to have a visualization of how the biomegrid is traversed
        // (2D plan):
        // http://i.imgur.com/s4whlZE.png
        // The big square grid represents our lower res biomegrid columns, and the very small
        // square grid
        // represents the normal biome grid columns (at block level) and the reason why it's
        // required to
        // re-scale it and do linear interpolation before densities can be used to generate raw
        // terrain.
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                double avgHeightScale = 0;
                double avgHeightBase = 0;
                double totalWeight = 0;
                Biome biome = GlowBiome.getBiome(biomeGrid[i + 2 + (j + 2) * 10]);
                BiomeHeight biomeHeight = HEIGHT_MAP.getOrDefault(biome, defaultHeight);
                // Sampling an average height base and scale by visiting the neighborhood
                // of the current biomegrid column.
                for (int m = 0; m < 5; m++) {
                    for (int n = 0; n < 5; n++) {
                        Biome nearBiome = GlowBiome.getBiome(biomeGrid[i + m + (j + n) * 10]);
                        BiomeHeight nearBiomeHeight = HEIGHT_MAP
                                .getOrDefault(nearBiome, defaultHeight);
                        double heightBase =
                                biomeHeightOffset + nearBiomeHeight.getHeight() * biomeHeightWeight;
                        double heightScale =
                                biomeScaleOffset + nearBiomeHeight.getScale() * biomeScaleWeight;
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

                noiseH = (noiseH * 0.2D + avgHeightBase) * baseSize / 8.0D * 4.0D + baseSize;
                for (int k = 0; k < 33; k++) {
                    // density should be lower and lower as we climb up, this gets a height value to
                    // subtract from the noise.
                    double nh = (k - noiseH) * stretchY * 128.0D / 256.0D / avgHeightScale;
                    if (nh < 0.0D) {
                        nh *= 4.0D;
                    }
                    double noiseR = roughnessNoise[index] / 512.0D;
                    double noiseR2 = roughnessNoise2[index] / 512.0D;
                    double noiseD = (detailNoise[index] / 10.0D + 1.0D) / 2.0D;
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR
                            : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens -= nh;
                    index++;
                    if (k > 29) {
                        double lowering = (k - 29) / 3.0D;
                        // linear interpolation
                        dens = dens * (1.0D - lowering) + -10.0D * lowering;
                    }
                    density[i][j][k] = dens;
                }
            }
        }
    }

    @RequiredArgsConstructor
    private static class BiomeHeight {

        public static final BiomeHeight DEFAULT = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEFAULT),
                getWorldConfig().getDouble(BIOME_SCALE_DEFAULT));
        public static final BiomeHeight FLAT_SHORE = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLAT_SHORE),
                getWorldConfig().getDouble(BIOME_SCALE_FLAT_SHORE));
        public static final BiomeHeight HIGH_PLATEAU = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_HIGH_PLATEAU),
                getWorldConfig().getDouble(BIOME_SCALE_HIGH_PLATEAU));
        public static final BiomeHeight FLATLANDS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLATLANDS),
                getWorldConfig().getDouble(BIOME_SCALE_FLATLANDS));
        public static final BiomeHeight SWAMPLAND = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_SWAMPLAND),
                getWorldConfig().getDouble(BIOME_SCALE_SWAMPLAND));
        public static final BiomeHeight MID_PLAINS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_PLAINS),
                getWorldConfig().getDouble(BIOME_SCALE_MID_PLAINS));
        public static final BiomeHeight FLATLANDS_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLATLANDS_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_FLATLANDS_HILLS));
        public static final BiomeHeight SWAMPLAND_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_SWAMPLAND_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_SWAMPLAND_HILLS));
        public static final BiomeHeight LOW_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_LOW_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_LOW_HILLS));
        public static final BiomeHeight HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_HILLS));
        public static final BiomeHeight MID_HILLS2 = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_HILLS2),
                getWorldConfig().getDouble(BIOME_SCALE_MID_HILLS2));
        public static final BiomeHeight DEFAULT_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEFAULT_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_DEFAULT_HILLS));
        public static final BiomeHeight MID_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_MID_HILLS));
        public static final BiomeHeight BIG_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_BIG_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_BIG_HILLS));
        public static final BiomeHeight BIG_HILLS2 = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_BIG_HILLS2),
                getWorldConfig().getDouble(BIOME_SCALE_BIG_HILLS2));
        public static final BiomeHeight EXTREME_HILLS = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_EXTREME_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_EXTREME_HILLS));
        public static final BiomeHeight ROCKY_SHORE = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_ROCKY_SHORE),
                getWorldConfig().getDouble(BIOME_SCALE_ROCKY_SHORE));
        public static final BiomeHeight LOW_SPIKES = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_LOW_SPIKES),
                getWorldConfig().getDouble(BIOME_SCALE_LOW_SPIKES));
        public static final BiomeHeight HIGH_SPIKES = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_HIGH_SPIKES),
                getWorldConfig().getDouble(BIOME_SCALE_HIGH_SPIKES));
        public static final BiomeHeight RIVER = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_RIVER),
                getWorldConfig().getDouble(BIOME_SCALE_RIVER));
        public static final BiomeHeight OCEAN = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_OCEAN),
                getWorldConfig().getDouble(BIOME_SCALE_OCEAN));
        public static final BiomeHeight DEEP_OCEAN = new BiomeHeight(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEEP_OCEAN),
                getWorldConfig().getDouble(BIOME_SCALE_DEEP_OCEAN));

        @Getter
        private final double height;
        @Getter
        private final double scale;
    }
}
