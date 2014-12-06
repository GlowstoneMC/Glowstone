package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class StoneBoulder {

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        while ((world.getBlockAt(sourceX, sourceY - 1, sourceZ).isEmpty() ||
                (world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() != Material.GRASS &&
                world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() != Material.DIRT &&
                world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() != Material.STONE)) && sourceY > 3) {
            sourceY--;
        }
        if (sourceY > 3 && world.getBlockAt(sourceX, sourceY + 1, sourceZ).isEmpty()) {
            for (int i = 0; i < 3; i++) {
                int radiusX = random.nextInt(2);
                int radiusZ = random.nextInt(2);
                int radiusY = random.nextInt(2);
                float f = (radiusX + radiusZ + radiusY) * 0.333F + 0.5F;
                for (int x = -radiusX; x <= radiusX; x++) {
                    for (int z = -radiusZ; z <= radiusZ; z++) {
                        for (int y = -radiusY; y <= radiusY; y++) {
                            if (x * x + z * z + y * y <= f * f) {
                                final Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                                block.setType(Material.MOSSY_COBBLESTONE);
                                block.setData((byte) 0);
                            }
                        }
                    }
                }
                sourceX += random.nextInt(4) - 1;
                sourceZ += random.nextInt(4) - 1;
                sourceY -= random.nextInt(2);
            }
        }
    }
}
