package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import java.util.Random;

public class WaterLilyDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);
        while (world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.AIR
            && sourceY > 0) {
            sourceY--;
        }

        for (int j = 0; j < 10; j++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (y >= 0 && y <= 255 && world.getBlockAt(x, y, z).getType() == Material.AIR
                && world.getBlockAt(x, y - 1, z).getType() == Material.WATER) {
                BlockState state = world.getBlockAt(x, y, z).getState();
                state.setType(Material.LILY_PAD);
                state.setData(new MaterialData(Material.LILY_PAD));
                state.update(true);
            }
        }
    }
}
