package net.glowstone.generator.populators;

import java.util.Random;

import net.glowstone.block.BlockID;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator that places yellow flowers, red roses, and tall grass.
 */
public class FlowerPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int cx = (source.getX() << 4) + x;
                int cz = (source.getZ() << 4) + z;
                int y = world.getHighestBlockYAt(cx, cz);

                Block block = source.getBlock(x, y, z);
                if (block.getTypeId() == BlockID.AIR
                        && block.getRelative(BlockFace.DOWN).getTypeId() == BlockID.GRASS) {
                    if (block.getBiome() == Biome.PLAINS) {
                        int n = random.nextInt(64);
                        if (n < 1) {
                            block.setTypeId(BlockID.RED_ROSE);
                        } else if (n < 4) {
                            block.setTypeId(BlockID.YELLOW_FLOWER);
                        }
                    } else if (block.getBiome() == Biome.SHRUBLAND
                            || block.getBiome() == Biome.SAVANNA) {
                        int n = random.nextInt(256);
                        if (n < 2) {
                            block.setTypeId(BlockID.RED_ROSE);
                        } else if (n < 3) {
                            block.setTypeId(BlockID.YELLOW_FLOWER);
                        } else if (n < 16) {
                            block.setTypeId(BlockID.LONG_GRASS);
                            block.setData((byte) 1);
                        }
                    } else if (block.getBiome() == Biome.FOREST
                            || block.getBiome() == Biome.SEASONAL_FOREST) {
                        int n = random.nextInt(256);
                        if (n < 16) {
                            block.setTypeId(BlockID.LONG_GRASS);
                            block.setData((byte) 2);
                        }
                    }
                }
            }
        }
    }
    
}
