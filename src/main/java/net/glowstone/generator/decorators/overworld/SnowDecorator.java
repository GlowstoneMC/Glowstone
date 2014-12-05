package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import net.glowstone.generator.decorators.BlockDecorator;

public class SnowDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = source.getX() << 4;
        int sourceZ = source.getZ() << 4;
        for (int x = sourceX; x < sourceX + 16; x++) {
            for (int z = sourceZ; z < sourceZ + 16; z++) {
                int y = world.getHighestBlockYAt(x, z) - 1;
                final Block block = world.getBlockAt(x, y, z);
                switch (block.getType()) {
                    case WATER:
                    case STATIONARY_WATER:
                        if (block.getData() == 0) {
                            block.setType(Material.ICE);
                        }
                        break;
                    case YELLOW_FLOWER:
                    case RED_ROSE:
                    case LONG_GRASS:
                    case LAVA:
                    case STATIONARY_LAVA:
                        break;
                    case DIRT:
                        block.setType(Material.GRASS);
                        if (block.getRelative(BlockFace.UP).isEmpty()) {
                            block.getRelative(BlockFace.UP).setType(Material.SNOW);
                        }
                        break;
                    default:
                        if (block.getRelative(BlockFace.UP).isEmpty()) {
                            block.getRelative(BlockFace.UP).setType(Material.SNOW);
                        }
                        break;
                }
            }
        }
    }
}
