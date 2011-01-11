package net.lightstone.world;

import net.lightstone.model.Chunk;

public final class TestWorldGenerator implements WorldGenerator {

	@Override
	public Chunk generate(int chunkX, int chunkZ) {
		Chunk chunk = new Chunk(chunkX, chunkZ);
		for (int x = 0; x < Chunk.WIDTH; x++) {
			for (int z = 0; z < Chunk.HEIGHT; z++) {
				for (int y = 0; y < Chunk.DEPTH; y++) {
					chunk.setType(x, z, y, y > 60 ? 0 : 4);
					chunk.setMetaData(x, z, y, 0);
					chunk.setBlockLight(x, z, y, 0);
					chunk.setSkyLight(x, z, y, 15);
				}
			}
		}
		return chunk;
	}

}
