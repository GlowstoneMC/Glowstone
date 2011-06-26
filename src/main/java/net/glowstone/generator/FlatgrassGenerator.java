package net.glowstone.generator;

import java.util.Random;
import org.bukkit.World;
import net.glowstone.GlowChunk;

/**
 * A simple {@link WorldGenerator} used to generate a "flat grass" world.
 * @author Graham Edgecombe
 */
public class FlatgrassGenerator extends GlowChunkGenerator {

    public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
        clear();
        
        for (int x = 0; x < GlowChunk.WIDTH; x++) {
            for (int z = 0; z < GlowChunk.HEIGHT; z++) {
                for (int y = 0; y < GlowChunk.DEPTH; y++) {
                    byte id = 0;
                    if (y == 60)
                        id = 2;
                    else if (y >= 55 && y < 60)
                        id = 3;
                    else if (y < 55)
                        id = 1;

                    set(x, y, z, id);
                }
            }
        }
        
        return data;
    }

}

