package net.glowstone.generator.ground;

import java.util.Random;
import lombok.Setter;
import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class GroundGenerator {

    @Setter
    private Material topMaterial;
    @Setter
    private Material groundMaterial;

    public GroundGenerator() {
        setTopMaterial(Material.GRASS_BLOCK);
        setGroundMaterial(Material.DIRT);
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

        Material topMat = topMaterial;
        Material groundMat = groundMaterial;

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
                            topMat = Material.AIR;
                            groundMat = Material.STONE;
                            chunkData.setBlock(x, y, z, Material.GRAVEL);
                        } else {
                            chunkData.setBlock(x, y, z, groundMat);
                        }
                    } else if (deep > 0) {
                        deep--;
                        chunkData.setBlock(x, y, z, groundMat);

                        if (deep == 0 && groundMat == Material.SAND) {
                            deep = random.nextInt(4) + Math.max(0, y - seaLevel - 1);
                            groundMat = Material.SANDSTONE;
                        }
                    }
                } else if (mat == Material.WATER && y == seaLevel - 2
                        && GlowBiomeClimate.isCold(biome, chunkX, y, chunkZ)) {
                    chunkData.setBlock(x, y, z, Material.ICE);
                }
            }
        }
    }
}
