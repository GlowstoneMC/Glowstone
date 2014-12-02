package net.glowstone.generator.populators;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * BlockPopulator that coats tundra and {@link Biome#TAIGA
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
                if (biome != Biome.COLD_TAIGA && biome != Biome.COLD_TAIGA_HILLS && biome != Biome.COLD_TAIGA_MOUNTAINS &&
                        biome != Biome.ICE_PLAINS && biome != Biome.ICE_MOUNTAINS && biome != Biome.ICE_PLAINS_SPIKES &&
                        biome != Biome.FROZEN_OCEAN && biome != Biome.FROZEN_RIVER && biome != Biome.COLD_BEACH) {
                    continue;
                }

                int y = snapshot.getHighestBlockYAt(x, z);
                if (source.getBlock(x, y, z).getType() == Material.AIR) {
                    y--;
                }

                Block block = source.getBlock(x, y, z);
                switch (block.getType()) {
                    case WATER:
                    case STATIONARY_WATER:
                        if (block.getData() == 0) {
                            block.setType(Material.ICE);
                        }
                        break;
                    case YELLOW_FLOWER:
                    case RED_ROSE:
                    case LONG_GRASS:
                    case LAVA:
                    case STATIONARY_LAVA:
                        break;
                    case DIRT:
                        block.setType(Material.GRASS);
                        if (block.getRelative(BlockFace.UP).isEmpty()) {
                            block.getRelative(BlockFace.UP).setType(Material.SNOW);
                        }
                        break;
                    default:
                        if (block.getRelative(BlockFace.UP).isEmpty()) {
                            block.getRelative(BlockFace.UP).setType(Material.SNOW);
                        }
                        break;
                }
            }
        }
    }
    
}
