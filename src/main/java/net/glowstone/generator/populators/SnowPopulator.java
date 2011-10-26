package net.glowstone.generator.populators;

import java.util.Random;

import net.glowstone.block.BlockID;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator that coats {@link Biome#TUNDRA tundra} and {@link Biome#TAIGA
 * taiga} with snow.
 */
public class SnowPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        int chunkX = source.getX() * 16;
        int chunkZ = source.getZ() * 16;
        ChunkSnapshot snapshot = source.getChunkSnapshot();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = world.getBiome(x + chunkX, z + chunkZ);
                if (biome != Biome.TAIGA && biome != Biome.TUNDRA) {
                    continue;
                }

                int y = snapshot.getHighestBlockYAt(x, z);
                if (source.getBlock(x, y, z).getTypeId() == BlockID.AIR) {
                    y--;
                }

                Block block = source.getBlock(x, y, z);
                switch (block.getType()) {
                    case WATER:
                    case STATIONARY_WATER:
                        if (block.getData() == 0) {
                            block.setTypeId(BlockID.ICE);
                        }
                        break;
                    case LAVA:
                    case STATIONARY_LAVA:
                        break;
                    case DIRT:
                        block.setTypeId(BlockID.GRASS);
                    default:
                        block.getRelative(BlockFace.UP).setTypeId(BlockID.SNOW);
                        break;
                }
            }
        }
    }
    
}
