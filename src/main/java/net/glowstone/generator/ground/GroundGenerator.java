package net.glowstone.generator.ground;

import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;

import java.util.Random;

public class GroundGenerator {

    protected static final MaterialData AIR = new MaterialData(Material.AIR);
    protected static final MaterialData STONE = new MaterialData(Material.STONE);
    protected static final MaterialData SANDSTONE = new MaterialData(Material.SANDSTONE);
    protected static final MaterialData GRASS = new MaterialData(Material.GRASS);
    protected static final MaterialData DIRT = new MaterialData(Material.DIRT);
    protected static final MaterialData COARSE_DIRT = new MaterialData(Material.DIRT, (byte) 1);
    protected static final MaterialData PODZOL = new MaterialData(Material.DIRT, (byte) 2);
    protected static final MaterialData GRAVEL = new MaterialData(Material.GRAVEL);
    protected static final MaterialData MYCEL = new MaterialData(Material.MYCEL);
    protected static final MaterialData SAND = new MaterialData(Material.SAND);
    protected static final MaterialData SNOW = new MaterialData(Material.SNOW_BLOCK);

    private MaterialData topMaterial;
    private MaterialData groundMaterial;

    public GroundGenerator() {
        setTopMaterial(GRASS);
        setGroundMaterial(DIRT);
    }

    /**
     * Generates a terrain column.
     *
     * @param chunkData the affected chunk
     * @param world the affected world
     * @param random the PRNG to use
     * @param x the chunk X coordinate
     * @param z the chunk Z coordinate
     * @param biome the biome this column is in
     * @param surfaceNoise the amplitude of random variation in surface height
     */
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {

        int seaLevel = world.getSeaLevel();

        MaterialData topMat = topMaterial;
        MaterialData groundMat = groundMaterial;

        int chunkX = x;
        int chunkZ = z;
        x &= 0xF;
        z &= 0xF;

        int surfaceHeight = Math
            .max((int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D), 1);
        int deep = -1;
        for (int y = 255; y >= 0; y--) {
            if (y <= random.nextInt(5)) {
                chunkData.setBlock(x, y, z, Material.BEDROCK);
            } else {
                Material mat = chunkData.getType(x, y, z);
                if (mat == Material.AIR) {
                    deep = -1;
                } else if (mat == Material.STONE) {
                    if (deep == -1) {
                        if (y >= seaLevel - 5 && y <= seaLevel) {
                            topMat = topMaterial;
                            groundMat = groundMaterial;
                        }

                        deep = surfaceHeight;
                        if (y >= seaLevel - 2) {
                            chunkData.setBlock(x, y, z, topMat);
                        } else if (y < seaLevel - 8 - surfaceHeight) {
                            topMat = AIR;
                            groundMat = STONE;
                            chunkData.setBlock(x, y, z, Material.GRAVEL);
                        } else {
                            chunkData.setBlock(x, y, z, groundMat);
                        }
                    } else if (deep > 0) {
                        deep--;
                        chunkData.setBlock(x, y, z, groundMat);

                        if (deep == 0 && groundMat.getItemType() == Material.SAND) {
                            deep = random.nextInt(4) + Math.max(0, y - seaLevel - 1);
                            groundMat = SANDSTONE;
                        }
                    }
                } else if (mat == Material.STATIONARY_WATER && y == seaLevel - 2
                        && GlowBiomeClimate.isCold(biome, chunkX, y, chunkZ)) {
                    chunkData.setBlock(x, y, z, Material.ICE);
                }
            }
        }
    }

    protected final void setTopMaterial(MaterialData topMaterial) {
        this.topMaterial = topMaterial;
    }

    protected final void setGroundMaterial(MaterialData groundMaterial) {
        this.groundMaterial = groundMaterial;
    }
}
