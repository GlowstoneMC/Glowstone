package net.glowstone.generator.ground;

import java.util.Arrays;
import java.util.Random;
import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;

public class MesaGroundGenerator extends GroundGenerator {

    protected static final MaterialData RED_SAND = new MaterialData(Material.SAND, (byte) 1);
    protected static final MaterialData ORANGE_STAINED_CLAY = new MaterialData(
        Material.STAINED_CLAY, (byte) 1);

    private final MesaType type;
    private final int[] colorLayer = new int[64];
    private MaterialData topMaterial;
    private MaterialData groundMaterial;
    private SimplexOctaveGenerator colorNoise;
    private SimplexOctaveGenerator canyonHeightNoise;
    private SimplexOctaveGenerator canyonScaleNoise;
    private long seed;

    public MesaGroundGenerator() {
        this(MesaType.NORMAL);
    }

    /**
     * Creates a ground generator for mesa biomes.
     *
     * @param type the type of mesa biome to generate
     */
    public MesaGroundGenerator(MesaType type) {
        this.type = type;
        topMaterial = RED_SAND;
        groundMaterial = ORANGE_STAINED_CLAY;
    }

    private void initialize(long seed) {
        if (seed != this.seed || colorNoise == null || canyonScaleNoise == null
            || canyonHeightNoise == null) {
            Random random = new Random(seed);
            colorNoise = new SimplexOctaveGenerator(random, 1);
            colorNoise.setScale(1 / 512.0D);
            initializeColorLayers(random);

            canyonHeightNoise = new SimplexOctaveGenerator(random, 4);
            canyonHeightNoise.setScale(1 / 4.0D);
            canyonScaleNoise = new SimplexOctaveGenerator(random, 1);
            canyonScaleNoise.setScale(1 / 512.0D);
            this.seed = seed;
        }
    }

    @Override
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {

        initialize(world.getSeed());

        int seaLevel = world.getSeaLevel();

        MaterialData topMat = topMaterial;
        MaterialData groundMat = groundMaterial;

        int surfaceHeight = Math
            .max((int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D), 1);
        boolean colored = Math.cos(surfaceNoise / 3.0D * Math.PI) <= 0;
        double bryceCanyonHeight = 0;
        if (type == MesaType.BRYCE) {
            int noiseX = (x & 0xFFFFFFF0) + (z & 0xF);
            int noiseZ = (z & 0xFFFFFFF0) + (x & 0xF);
            double noiseCanyonHeight = Math
                .min(Math.abs(surfaceNoise), canyonHeightNoise.noise(noiseX, noiseZ, 0.5D, 2.0D));
            if (noiseCanyonHeight > 0) {
                double heightScale = Math.abs(canyonScaleNoise.noise(noiseX, noiseZ, 0.5D, 2.0D));
                bryceCanyonHeight = Math.pow(noiseCanyonHeight, 2) * 2.5D;
                double maxHeight = Math.ceil(50 * heightScale) + 14;
                if (bryceCanyonHeight > maxHeight) {
                    bryceCanyonHeight = maxHeight;
                }
                bryceCanyonHeight += seaLevel;
            }
        }

        int chunkX = x;
        x &= 0xF;
        z &= 0xF;

        int deep = -1;
        boolean groundSet = false;
        for (int y = 255; y >= 0; y--) {
            if (y < (int) bryceCanyonHeight && chunkData.getType(x, y, z) == Material.AIR) {
                chunkData.setBlock(x, y, z, Material.STONE);
            }
            if (y <= random.nextInt(5)) {
                chunkData.setBlock(x, y, z, Material.BEDROCK);
            } else {
                Material mat = chunkData.getType(x, y, z);
                if (mat == Material.AIR) {
                    deep = -1;
                } else if (mat == Material.STONE) {
                    if (deep == -1) {
                        groundSet = false;
                        if (y >= seaLevel - 5 && y <= seaLevel) {
                            groundMat = groundMaterial;
                        }

                        deep = surfaceHeight + Math.max(0, y - seaLevel - 1);
                        if (y >= seaLevel - 2) {
                            if (type == MesaType.FOREST && y > seaLevel + 22 + (surfaceHeight
                                << 1)) {
                                topMat = colored ? GRASS : COARSE_DIRT;
                                chunkData.setBlock(x, y, z, topMat);
                            } else if (y > seaLevel + 2 + surfaceHeight) {
                                int color = colorLayer[(y + (int) Math
                                    .round(colorNoise.noise(chunkX, chunkX, 0.5D, 2.0D) * 2.0D))
                                    % colorLayer.length];
                                setColoredGroundLayer(chunkData, x, y, z,
                                    y < seaLevel || y > 128 ? 1 : colored ? color : -1);
                            } else {
                                chunkData.setBlock(x, y, z, topMaterial);
                                groundSet = true;
                            }
                        } else {
                            chunkData.setBlock(x, y, z, groundMat);
                        }
                    } else if (deep > 0) {
                        deep--;
                        if (groundSet) {
                            chunkData.setBlock(x, y, z, groundMaterial);
                        } else {
                            int color = colorLayer[(y + (int) Math
                                .round(colorNoise.noise(chunkX, chunkX, 0.5D, 2.0D) * 2.0D))
                                % colorLayer.length];
                            setColoredGroundLayer(chunkData, x, y, z, color);
                        }
                    }
                }
            }
        }
    }

    private void setColoredGroundLayer(ChunkData chunkData, int x, int y, int z, int color) {
        if (color >= 0) {
            chunkData.setBlock(x, y, z, new MaterialData(Material.STAINED_CLAY, (byte) color));
        } else {
            chunkData.setBlock(x, y, z, Material.HARD_CLAY);
        }
    }

    private void setRandomLayerColor(Random random, int minLayerCount, int minLayerHeight,
        int color) {
        for (int i = 0; i < random.nextInt(4) + minLayerCount; i++) {
            int j = random.nextInt(colorLayer.length);
            int k = 0;
            while (k < random.nextInt(3) + minLayerHeight && j < colorLayer.length) {
                colorLayer[j++] = color;
                k++;
            }
        }
    }

    private void initializeColorLayers(Random random) {
        Arrays.fill(colorLayer, -1); // hard clay, other values are stained clay
        int i = 0;
        while (i < colorLayer.length) {
            i += random.nextInt(5) + 1;
            if (i < colorLayer.length) {
                colorLayer[i++] = 1; // orange
            }
        }
        setRandomLayerColor(random, 2, 1, 4); // yellow
        setRandomLayerColor(random, 2, 2, 12); // brown
        setRandomLayerColor(random, 2, 1, 14); // red
        int j = 0;
        for (i = 0; i < random.nextInt(3) + 3; i++) {
            j += random.nextInt(16) + 4;
            if (j >= colorLayer.length) {
                break;
            }
            if (random.nextInt(2) == 0 || j < colorLayer.length - 1 && random.nextInt(2) == 0) {
                colorLayer[j - 1] = 8; // light gray
            } else {
                colorLayer[j] = 0; // white
            }
        }
    }

    public enum MesaType {
        NORMAL,
        BRYCE,
        FOREST
    }
}
