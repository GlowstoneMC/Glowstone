package net.glowstone.world;

import net.glowstone.GlowChunk;

/**
 * A simple {@link WorldGenerator} used to generate a "flat grass" world.
 * @author Graham Edgecombe
 */
public class FlatGrassWorldGenerator implements WorldGenerator {

	@Override
	public GlowChunk generate(int chunkX, int chunkZ) {
		GlowChunk chunk = new GlowChunk(chunkX, chunkZ);
		for (int x = 0; x < GlowChunk.WIDTH; x++) {
			for (int z = 0; z < GlowChunk.HEIGHT; z++) {
				for (int y = 0; y < GlowChunk.DEPTH; y++) {
					int id = 0;
					if (y == 60)
						id = 2;
					else if (y >= 55 && y < 60)
						id = 3;
					else if (y < 55)
						id = 1;

					chunk.setType(x, z, y, id);
					chunk.setMetaData(x, z, y, 0);
					chunk.setBlockLight(x, z, y, 0);
					chunk.setSkyLight(x, z, y, 15);
				}
			}
		}
		return chunk;
	}

}

