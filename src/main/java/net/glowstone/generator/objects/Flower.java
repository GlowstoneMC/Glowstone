package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Flower {

    final Material type;
    final int data;

    public Flower(FlowerType plantType) {
        type = plantType.getType();
        data = plantType.getData();
    }

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            final Block block = world.getBlockAt(x, y, z);
            if (y < 255 && block.getType() == Material.AIR &&
                    block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
                block.setType(type);
                block.setData((byte) data);
            }
        }
    }
}
