package net.glowstone.generator.populators;

import java.util.Random;

import net.glowstone.block.BlockID;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator that turns deserts into sand and places cacti.
 */
public class DesertPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        boolean nether = world.getEnvironment() == Environment.NETHER;
        int matSand = nether ? BlockID.SOUL_SAND : BlockID.SAND;
        int matDirt = nether ? BlockID.NETHERRACK : BlockID.DIRT;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int tx = (chunk.getX() << 4) + x;
                int tz = (chunk.getZ() << 4) + z;
                int y = world.getHighestBlockYAt(tx, tz);

                Block block = chunk.getBlock(x, y, z).getRelative(BlockFace.DOWN);
                if (block.getBiome() != Biome.DESERT) {
                    continue;
                }

                // Set top few layers of grass/dirt to sand
                for (int i = 0; i < 5; ++i) {
                    Block b2 = block.getRelative(0, -i, 0);
                    if (b2.getTypeId() == BlockID.GRASS || b2.getTypeId() == matDirt) {
                        b2.setTypeId(matSand);
                    }
                }

                // Generate cactus
                if (block.getTypeId() == matSand) {
                    if (random.nextInt(100) == 0) {
                        // Make sure it's surrounded by air
                        Block base = block.getRelative(BlockFace.UP);
                        if (base.getTypeId() == 0
                                && base.getRelative(BlockFace.NORTH).getTypeId() == 0
                                && base.getRelative(BlockFace.EAST).getTypeId() == 0
                                && base.getRelative(BlockFace.SOUTH).getTypeId() == 0
                                && base.getRelative(BlockFace.WEST).getTypeId() == 0) {
                            generateCactus(base, random.nextInt(4));
                        }
                    }
                }
            }
        }
    }

    private static void generateCactus(Block block, int height) {
        if (block.getWorld().getEnvironment() == Environment.NETHER) {
            block.setTypeId(BlockID.FIRE);
        } else {
            for (int i = 0; i < height; ++i) {
                block.getRelative(0, i, 0).setTypeId(BlockID.CACTUS);
            }
        }
    }
    
}
