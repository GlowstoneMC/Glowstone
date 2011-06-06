package net.glowstone.world;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

/**
 * A simple {@link WorldGenerator} used to generate the Nether equivalent of
 * a "flat grass" world.
 * @author Tad
 */
public class FlatNetherWorldGenerator implements WorldGenerator {

	public GlowChunk generate(GlowWorld world, int chunkX, int chunkZ) {
		GlowChunk chunk = new GlowChunk(world, chunkX, chunkZ);
		for (int x = 0; x < GlowChunk.WIDTH; x++) {
			for (int z = 0; z < GlowChunk.HEIGHT; z++) {
				for (int y = 0; y < GlowChunk.DEPTH; y++) {
					int id = 0;
					if (y <= 60)
						id = 87;
					else if (y < 4)
						id = 7;

					chunk.setType(x, z, y, id);
					chunk.setMetaData(x, z, y, 0);
					chunk.setBlockLight(x, z, y, 0);
					chunk.setSkyLight(x, z, y, 15);
				}
			}
		}
		return chunk;
	}

    public long getSeed() {
        return 0;
    }

}

