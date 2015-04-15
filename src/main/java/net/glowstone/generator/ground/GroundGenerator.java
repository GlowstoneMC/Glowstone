package net.glowstone.generator.ground;

import java.util.Random;

import net.glowstone.constants.GlowBiomeClimate;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class GroundGenerator {

    private Material topMaterial;
    private int topMaterialData;
    private Material groundMaterial;
    private int groundMaterialData;

    public GroundGenerator() {
        setTopMaterial(Material.GRASS);
        setGroundMaterial(Material.DIRT);
    }

    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {

        int seaLevel = world.getSeaLevel();

        Material topMat = topMaterial;
        int topMatData = topMaterialData;
        Material groundMat = groundMaterial;
        int groundMatData = groundMaterialData;

        int chunkX = x;
        int chunkZ = z;
        x &= 0xF;
        z &= 0xF;

        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D), 1);
        int deep = -1;
        for (int y = 255; y >= 0; y--) {
            if (y <= random.nextInt(5)) {
                set(buf, x, y, z, Material.BEDROCK);
            } else {
                Material mat = get(buf, x, y, z);
                if (mat == Material.AIR) {
                    deep = -1;
                } else if (mat == Material.STONE) {
                    if (deep == -1) {
                        if (y >= seaLevel - 5 && y <= seaLevel) {
                            topMat = topMaterial;
                            topMatData = topMaterialData;
                            groundMat = groundMaterial;
                            groundMatData = groundMaterialData;
                        }

                        deep = surfaceHeight;
                        if (y >= seaLevel - 2) {
                            set(buf, x, y, z, topMat, topMatData);
                        } else if (y < seaLevel - 8 - surfaceHeight) {
                            topMat = Material.AIR;
                            topMatData = 0;
                            groundMat = Material.STONE;
                            groundMatData = 0;
                            set(buf, x, y, z, Material.GRAVEL);
                        } else {
                            set(buf, x, y, z, groundMat, groundMatData);
                        }
                    } else if (deep > 0) {
                        deep--;
                        set(buf, x, y, z, groundMat, groundMatData);

                        if (deep == 0 && groundMat == Material.SAND) {
                            deep = random.nextInt(4) + Math.max(0, y - seaLevel - 1);
                            groundMat = Material.SANDSTONE;
                        }
                    }
                } else if (mat == Material.STATIONARY_WATER && y == seaLevel - 2 &&
                        GlowBiomeClimate.isCold(biome, chunkX, y, chunkZ)) {
                    set(buf, x, y, z, Material.ICE);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected final Material get(short[][] buf, int x, int y, int z) {
        if (buf[y >> 4] == null) {
            return Material.AIR;
        }
        return Material.getMaterial(buf[y >> 4][((y & 0xF) << 8) | (z << 4) | x] >> 4);
    }

    protected final void set(short[][] buf, int x, int y, int z, Material id) {
        set(buf, x, y, z, id, 0);
    }

    @SuppressWarnings("deprecation")
    protected final void set(short[][] buf, int x, int y, int z, Material id, int data) {
        if (id.getId() == 0) {
            return;
        }
        if (buf[y >> 4] == null) {
            buf[y >> 4] = new short[4096];
        }
        buf[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (short) (((id.getId() << 4) & 0xFFF0) | data);
    }

    protected final void setTopMaterial(Material topMaterial) {
        setTopMaterial(topMaterial, 0);
    }

    protected final void setTopMaterial(Material topMaterial, int topMaterialData) {
        this.topMaterial = topMaterial;
        this.topMaterialData = topMaterialData;
    }

    protected final void setGroundMaterial(Material groundMaterial) {
        setGroundMaterial(groundMaterial, 0);
    }

    protected final void setGroundMaterial(Material groundMaterial, int groundMaterialData) {
        this.groundMaterial = groundMaterial;
        this.groundMaterialData = groundMaterialData;
    }
}
