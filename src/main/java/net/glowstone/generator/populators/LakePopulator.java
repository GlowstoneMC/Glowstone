package net.glowstone.generator.populators;

import java.util.ArrayList;
import java.util.Random;

import net.glowstone.block.BlockID;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * BlockPopulator that generates water and lava lakes.
 */
public class LakePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (random.nextInt(10) > 1) {
            return;
        }

        ChunkSnapshot snapshot = source.getChunkSnapshot();

        int rx16 = random.nextInt(16);
        int rx = (source.getX() << 4) + rx16;
        int rz16 = random.nextInt(16);
        int rz = (source.getZ() << 4) + rz16;
        if (snapshot.getHighestBlockYAt(rx16, rz16) < 4) {
            return;
        }
        int ry = 6 + random.nextInt(snapshot.getHighestBlockYAt(rx16, rz16) - 3);
        int radius = 2 + random.nextInt(3);

        int liquidMaterial = BlockID.LAVA;
        int solidMaterial = BlockID.OBSIDIAN;

        if (random.nextInt(10) < 3) {
            ry = snapshot.getHighestBlockYAt(rx16, rz16) - 1;
        }
        if (random.nextInt(96) < ry && world.getEnvironment() != Environment.NETHER) {
            liquidMaterial = BlockID.WATER;
            solidMaterial = BlockID.WATER;
        } else if (world.getBlockAt(rx, ry, rz).getBiome() == Biome.FOREST
                || world.getBlockAt(rx, ry, rz).getBiome() == Biome.SEASONAL_FOREST) {
            return;
        }

        ArrayList<Block> lakeBlocks = new ArrayList<Block>();
        for (int i = -1; i < 4; i++) {
            Vector center = new BlockVector(rx, ry - i, rz);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector position = center.clone().add(new Vector(x, 0, z));
                    if (center.distance(position) <= radius + 0.5 - i) {
                        lakeBlocks.add(world.getBlockAt(position.toLocation(world)));
                    }
                }
            }
        }

        for (Block block : lakeBlocks) {
            // Ensure it's not air or liquid already
            if (!block.isEmpty() && !block.isLiquid()) {
                if (block.getY() == ry + 1) {
                    if (random.nextBoolean()) {
                        block.setTypeId(BlockID.AIR);
                    }
                } else if (block.getY() == ry) {
                    block.setTypeId(BlockID.AIR);
                } else if (random.nextInt(10) > 1) {
                    block.setTypeId(liquidMaterial);
                } else {
                    block.setTypeId(solidMaterial);
                }
            }
        }
    }
    
}
