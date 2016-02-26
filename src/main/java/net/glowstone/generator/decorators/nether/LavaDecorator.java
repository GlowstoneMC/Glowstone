package net.glowstone.generator.decorators.nether;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.generator.decorators.BlockDecorator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class LavaDecorator extends BlockDecorator {

    private static final BlockFace[] SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN};

    private boolean flowing;

    public LavaDecorator() {
        this(false);
    }

    public LavaDecorator(boolean flowing) {
        this.flowing = flowing;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = flowing ? 4 + random.nextInt(120) : 10 + random.nextInt(108);

        final Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
        if ((block.getType() == Material.NETHERRACK || block.isEmpty()) &&
                block.getRelative(BlockFace.UP).getType() == Material.NETHERRACK) {
            int netherrackBlockCount = 0;
            for (BlockFace face : SIDES) {
                if (block.getRelative(face).getType() == Material.NETHERRACK) {
                    netherrackBlockCount++;
                }
            }
            int airBlockCount = 0;
            for (BlockFace face : SIDES) {
                if (block.getRelative(face).isEmpty()) {
                    airBlockCount++;
                }
            }

            if (netherrackBlockCount == 5 || (flowing && airBlockCount == 1 && netherrackBlockCount == 4)) {
                final BlockState state = block.getState();
                state.setType(Material.LAVA);
                state.update(true);
                // 10 instead of the default 5 because lava should flow faster in the nether
                ((GlowWorld) block.getWorld()).requestPulse((GlowBlock) block, 10);
            }
        }
    }
}
