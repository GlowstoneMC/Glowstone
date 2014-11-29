package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import net.glowstone.generator.decorators.BlockDecorator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

public class EmeraldOreDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4);
        int sourceZ = (source.getZ() << 4);

        for (int i = 0; i < random.nextInt(6) + 3; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(28) + 4;

            if (world.getBlockAt(x, y, z).getType() == Material.STONE) {
                world.getBlockAt(x, y, z).setType(Material.EMERALD_ORE);
            }
        }
    }
}
