package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.LongGrass;

public class TallGrass {

    private final LongGrass grassType;

    public TallGrass(LongGrass grassType) {
        this.grassType = grassType;
    }

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        while ((world.getBlockAt(sourceX, sourceY, sourceZ).isEmpty() ||
                world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.LEAVES) &&
                sourceY > 0) {
            sourceY--;
        }
        for (int i = 0; i < 128; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            final Block block = world.getBlockAt(x, y, z);
            if (y < 255 && block.getType() == Material.AIR && (block.getRelative(BlockFace.DOWN).getType() == Material.GRASS ||
                    block.getRelative(BlockFace.DOWN).getType() == Material.DIRT)) {
                final BlockState state = block.getState();
                state.setType(Material.LONG_GRASS);
                state.setData(grassType);
                state.update(true);
            }
        }
    }
}
