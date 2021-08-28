package net.glowstone.generator.decorators.overworld;

import net.glowstone.block.GlowBlock;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.scheduler.PulseTask;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class FlowingLiquidDecorator extends BlockDecorator {

    private static final BlockFace[] SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST};
    private final Material type;

    /**
     * Creates a decorator that carves out waterfalls or lavafalls in stone walls.
     *
     * @param type {@link Material#WATER} or {@link Material#LAVA}
     */
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
        int sourceY = random
            .nextInt(random.nextInt(type == Material.LAVA ? random.nextInt(240) + 8 : 248) + 8);

        Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
        if ((block.getType() == Material.STONE || block.getType() == Material.AIR)
            && block.getRelative(BlockFace.DOWN).getType() == Material.STONE
            && block.getRelative(BlockFace.UP).getType() == Material.STONE) {
            int stoneBlockCount = 0;
            for (BlockFace face : SIDES) {
                if (block.getRelative(face).getType() == Material.STONE) {
                    stoneBlockCount++;
                }
            }
            if (stoneBlockCount == 3) {
                int airBlockCount = 0;
                for (BlockFace face : SIDES) {
                    if (block.getRelative(face).isEmpty()) {
                        airBlockCount++;
                    }
                }
                if (airBlockCount == 1) {
                    BlockState state = block.getState();
                    state.setType(type);
                    state.update(true);
                    new PulseTask((GlowBlock) state.getBlock(), true, 1, true).startPulseTask();
                }
            }
        }
    }
}
