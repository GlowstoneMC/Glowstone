package net.glowstone.generator.populators;

import java.util.Random;

import net.glowstone.block.BlockID;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator that generates giant mushrooms in {@link Biome#TUNDRA tundra}
 * and {@link Biome#TAIGA taiga}, a la Minecraft 1.8.
 */
public class MushroomPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (random.nextInt(16) > 0) {
            return;
        }

        int rx = 2 + random.nextInt(12);
        int rz = 2 + random.nextInt(12);
        Block block = source.getBlock(rx, world.getHighestBlockYAt((source.getX() << 4)
                + rx, (source.getZ() << 4) + rz), rz);
        if (block.getBiome() != Biome.TAIGA && block.getBiome() != Biome.TUNDRA) {
            return;
        }
        if (block.getRelative(BlockFace.DOWN).getTypeId() != BlockID.GRASS
                && block.getRelative(BlockFace.DOWN).getTypeId() != BlockID.DIRT) {
            return;
        }

        int size = 2 + random.nextInt(4);
        for (int i = 0; i <= size + 1; i++) {
            Block mushroom = block.getRelative(0, i, 0);
            mushroom.setTypeIdAndData(BlockID.LOG, (byte) 2, true);

            if (i >= size) {
                int diff = i - size;
                int diffNext = i - size + 1;
                for (int x = -size + diff; x <= size - diff; x++) {
                    for (int z = -size + diff; z <= size - diff; z++) {
                        if (x * x + z * z < (size - diff) * (size - diff)
                                && (i > size || x * x + z * z + 1 > (size - diffNext)
                                * (size - diffNext))) {
                            mushroom.getRelative(x, 0, z).setTypeId(BlockID.STONE);
                        }
                    }
                }
            }
        }
    }
    
}
