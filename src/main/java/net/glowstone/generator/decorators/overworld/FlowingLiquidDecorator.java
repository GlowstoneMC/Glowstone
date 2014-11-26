package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import net.glowstone.generator.decorators.BlockDecorator;

public class FlowingLiquidDecorator extends BlockDecorator {

    private final Material type;

    public FlowingLiquidDecorator(Material type) {
        this.type = type;
        if (type != Material.WATER && type != Material.LAVA) {
            throw new IllegalArgumentException("Flowing liquid must be WATER or LAVA");
        }
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(random.nextInt(type == Material.LAVA ? random.nextInt(240) + 8 : 248) + 8);

        final Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
        if (block.getType() != Material.STONE && block.getType() != Material.AIR &&
                block.getRelative(BlockFace.DOWN).getType() == Material.STONE &&
                block.getRelative(BlockFace.UP).getType() == Material.STONE) {
            int stoneBlockCount = 0;
            if (block.getRelative(BlockFace.EAST).getType() == Material.STONE) {
                stoneBlockCount++;
            }
            if (block.getRelative(BlockFace.WEST).getType() == Material.STONE) {
                stoneBlockCount++;
            }
            if (block.getRelative(BlockFace.NORTH).getType() == Material.STONE) {
                stoneBlockCount++;
            }
            if (block.getRelative(BlockFace.SOUTH).getType() == Material.STONE) {
                stoneBlockCount++;
            }            
            if (stoneBlockCount == 3) {
                int airBlockCount = 0;
                if (block.getRelative(BlockFace.EAST).isEmpty()) {
                    airBlockCount++;
                }
                if (block.getRelative(BlockFace.WEST).isEmpty()) {
                    airBlockCount++;
                }
                if (block.getRelative(BlockFace.NORTH).isEmpty()) {
                    airBlockCount++;
                }
                if (block.getRelative(BlockFace.SOUTH).isEmpty()) {
                    airBlockCount++;
                }
                if (airBlockCount == 1) {
                    final BlockState state = block.getState();
                    state.setType(type);
                    state.update(true);
                }
            }
        }
    }
}
