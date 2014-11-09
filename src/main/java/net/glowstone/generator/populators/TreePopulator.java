package net.glowstone.generator.populators;

import net.glowstone.generator.TreeGenerator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * BlockPopulator that adds trees based on the biome.
 */
public class TreePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);

        TreeType type = TreeType.TREE;
        int chance = 0;
        int multiplier = 1;

        if (random.nextBoolean()) {
            type = TreeType.BIRCH;
        }

        switch (world.getBlockAt(centerX, 0, centerZ).getBiome()) {
            case OCEAN:
                // workaround for lack of biome generation
            case FOREST:
                chance = 160;
                multiplier = 10;
                break;
            case PLAINS:
                chance = 40;
                break;
            case SAVANNA:
                chance = 20;
                type = TreeType.ACACIA;
                break;
            case SWAMPLAND:
                chance = 120;
                type = TreeType.SWAMP;
                break;
            case TAIGA:
                chance = 120;
                multiplier = 3;
                type = TreeType.REDWOOD;
                break;
            case SKY:
                chance = 1;
                break;
            case DESERT:
            case HELL:
                return;
        }

        final TreeGenerator generator = new TreeGenerator();
        for (int i = 0; i < multiplier; i++) {
            centerX = (source.getX() << 4) + random.nextInt(16);
            centerZ = (source.getZ() << 4) + random.nextInt(16);
            if (random.nextInt(300) < chance) {
                int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
                Block sourceBlock = world.getBlockAt(centerX, centerY, centerZ);

                if (sourceBlock.getType() == Material.GRASS) {
                    generator.generate(random, sourceBlock.getLocation().add(0, 1, 0), type);
                }
            }
        }
    }

}
