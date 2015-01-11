package net.glowstone.generator.ground;

import java.util.Arrays;
import java.util.Random;

import net.glowstone.util.noise.SimplexOctaveGenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class MesaGroundGenerator extends GroundGenerator {

    private final MesaType type;
    private final int[] colorLayer = new int[64];
    private Material topMaterial;
    private int topMaterialData;
    private Material groundMaterial;
    private int groundMaterialData;
    private SimplexOctaveGenerator colorNoise;
    private SimplexOctaveGenerator canyonHeightNoise;
    private SimplexOctaveGenerator canyonScaleNoise;
    private long seed;

    private void initialize(long seed) {
        if (seed != this.seed || colorNoise == null || canyonScaleNoise == null || canyonHeightNoise == null) {
            final Random random = new Random(seed);
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

    public MesaGroundGenerator() {
        this(MesaType.NORMAL);
    }

    public MesaGroundGenerator(MesaType type) {
        this.type = type;
        topMaterial = Material.SAND;
        topMaterialData = 1;    // orange sand
        groundMaterial = Material.STAINED_CLAY;
        groundMaterialData = 1; // orange stained clay
    }

    @Override
    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {

        initialize(world.getSeed());

        int seaLevel = world.getSeaLevel();

        Material topMat = topMaterial;
        int topMatData = topMaterialData;
        Material groundMat = groundMaterial;
        int groundMatData = groundMaterialData;

        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D), 1);
        boolean colored = Math.cos(surfaceNoise / 3.0D * Math.PI) > 0 ? false : true;
        double bryceCanyonHeight = 0;
        if (type == MesaType.BRYCE) {
            int nX = (x & 0xFFFFFFF0) + (z & 0xF);
            int nZ = (z & 0xFFFFFFF0) + (x & 0xF);
            double noiseCanyonHeight = Math.min(Math.abs(surfaceNoise), canyonHeightNoise.noise(nX, nZ, 0.5D, 2.0D));
            if (noiseCanyonHeight > 0) {
                double heightScale = Math.abs(canyonScaleNoise.noise(nX, nZ, 0.5D, 2.0D));
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
            if (y < (int) bryceCanyonHeight && get(buf, x, y, z) == Material.AIR) {
                set(buf, x, y, z, Material.STONE);
            }
            if (y <= random.nextInt(5)) {
                set(buf, x, y, z, Material.BEDROCK);
            } else {
                Material mat = get(buf, x, y, z);
                if (mat == Material.AIR) {
                    deep = -1;
                } else if (mat == Material.STONE) {
                    if (deep == -1) {
                        groundSet = false;
                        if (y >= seaLevel - 5 && y <= seaLevel) {
                            groundMat = groundMaterial;
                            groundMatData = groundMaterialData;
                        }

                        deep = surfaceHeight + Math.max(0, y - seaLevel - 1);
                        if (y >= seaLevel - 2) {
                            if (type == MesaType.FOREST && y > seaLevel + 22 + surfaceHeight * 2) {
                                topMat = colored ? Material.GRASS : Material.DIRT;
                                topMatData = colored ? 0 : 1; // grass or coarse dirt
                                set(buf, x, y, z, topMat, topMatData);
                            } else if (y > seaLevel + 2 + surfaceHeight) {
                                int color = colorLayer[(y + (int) Math.round(colorNoise.noise(chunkX, chunkX, 0.5D, 2.0D) * 2.0D)) % colorLayer.length];
                                setColoredGroundLayer(buf, x, y, z, y < seaLevel || y > 128 ? 1 : colored ? color : -1);
                            } else {
                                set(buf, x, y, z, topMaterial, topMaterialData);
                                groundSet = true;
                            }
                        } else {
                            set(buf, x, y, z, groundMat, groundMatData);
                        }
                    } else if (deep > 0) {
                        deep--;
                        if (groundSet) {
                            set(buf, x, y, z, groundMaterial, groundMaterialData);
                        } else {
                            int color = colorLayer[(y + (int) Math.round(colorNoise.noise(chunkX, chunkX, 0.5D, 2.0D) * 2.0D)) % colorLayer.length];
                            setColoredGroundLayer(buf, x, y, z, color);
                        }
                    }
                }
            }
        }
    }

    public static enum MesaType {
        NORMAL,
        BRYCE,
        FOREST
    }

    private void setColoredGroundLayer(short[][] buf, int x, int y, int z, int color) {
        if (color >= 0) {
            set(buf, x, y, z, Material.STAINED_CLAY, color);
        } else {
            set(buf, x, y, z, Material.HARD_CLAY);
        }
    }

    private void setRandomLayerColor(Random random, int minLayerCount, int minLayerHeight, int color) {
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
            if (random.nextInt(2) == 0 || (j < colorLayer.length - 1 && random.nextInt(2) == 0)) {
                colorLayer[j - 1] = 8; // light gray
            } else {
                colorLayer[j] = 0; // white
            }
        }
    }
}
