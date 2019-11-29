package net.glowstone.generator.decorators.overworld;

import java.util.Random;
import net.glowstone.generator.objects.BlockPatch;
import net.glowstone.generator.objects.IceSpike;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class IceDecorator extends BlockPopulator {

    private static final Material[] OVERRIDABLES = {Material.DIRT, Material.GRASS_BLOCK,
        Material.SNOW_BLOCK, Material.ICE};

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;

        for (int i = 0; i < 3; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z) - 1;
            while (y > 2 && world.getBlockAt(x, y, z).isEmpty()) {
                y--;
            }
            if (world.getBlockAt(x, y, z).getType() == Material.SNOW_BLOCK) {
                new BlockPatch(Material.PACKED_ICE, 4, 1, OVERRIDABLES)
                    .generate(world, random, x, y, z);
            }
        }

        for (int i = 0; i < 2; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            while (y > 2 && world.getBlockAt(x, y, z).isEmpty()) {
                y--;
            }
            if (world.getBlockAt(x, y, z).getType() == Material.SNOW_BLOCK) {
                new IceSpike().generate(world, random, x, y, z);
            }
        }
    }
}
