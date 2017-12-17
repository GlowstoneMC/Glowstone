package net.glowstone.generator.populators.overworld;

import java.util.Random;
import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class SnowPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        int sourceX = source.getX() << 4;
        int sourceZ = source.getZ() << 4;
        for (int x = sourceX; x < sourceX + 16; x++) {
            for (int z = sourceZ; z < sourceZ + 16; z++) {
                int y = world.getHighestBlockYAt(x, z) - 1;
                if (GlowBiomeClimate.isSnowy(world.getBiome(x, z), sourceX + x, y, sourceZ + z)) {
                    Block block = world.getBlockAt(x, y, z);
                    Block blockAbove = block.getRelative(BlockFace.UP);
                    switch (block.getType()) {
                        case WATER:
                        case STATIONARY_WATER:
                        case SNOW:
                        case ICE:
                        case PACKED_ICE:
                        case YELLOW_FLOWER:
                        case RED_ROSE:
                        case LONG_GRASS:
                        case DOUBLE_PLANT:
                        case SUGAR_CANE:
                        case LAVA:
                        case STATIONARY_LAVA:
                            break;
                        case DIRT:
                            block.setType(Material.GRASS);
                            if (blockAbove.isEmpty()) {
                                blockAbove.setType(Material.SNOW);
                            }
                            break;
                        default:
                            if (blockAbove.isEmpty()) {
                                blockAbove.setType(Material.SNOW);
                            }
                            break;
                    }
                }
            }
        }
    }
}
