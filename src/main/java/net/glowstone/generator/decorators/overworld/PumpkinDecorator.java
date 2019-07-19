package net.glowstone.generator.decorators.overworld;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.Pumpkin;
import org.jetbrains.annotations.NotNull;

public class PumpkinDecorator extends BlockPopulator {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Override
    public void populate(@NotNull World world, Random random, @NotNull Chunk source) {
        if (random.nextInt(32) == 0) {
            int sourceX = (source.getX() << 4) + random.nextInt(16);
            int sourceZ = (source.getZ() << 4) + random.nextInt(16);
            int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);

            for (int i = 0; i < 64; i++) {
                int x = sourceX + random.nextInt(8) - random.nextInt(8);
                int z = sourceZ + random.nextInt(8) - random.nextInt(8);
                int y = sourceY + random.nextInt(4) - random.nextInt(4);

                if (world.getBlockAt(x, y, z).getType() == Material.AIR
                        && world.getBlockAt(x, y - 1, z).getType() == Material.GRASS_BLOCK) {
                    BlockState state = world.getBlockAt(x, y, z).getState();
                    state.setType(Material.PUMPKIN);
                    // random facing
                    state.setData(new Pumpkin(FACES[random.nextInt(FACES.length)]));
                    state.update(true);
                }
            }
        }
    }
}
