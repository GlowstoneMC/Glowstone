package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class DoublePlant {

    final Material type;
    final int data;

    public DoublePlant(DoublePlantType plantType) {
        type = plantType.getType();
        data = plantType.getData();
    }

    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean placed = false;
        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            final Block block = world.getBlockAt(x, y, z);
            if (y < 255 && block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty() &&
                    block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
                block.setType(type);
                block.setData((byte) data);
                block.getRelative(BlockFace.UP).setType(type);
                block.getRelative(BlockFace.UP).setData((byte) 8);
                placed = true;
            }
        }
        return placed;
    }
}
