package net.lightstone.world;

import net.lightstone.model.Chunk;

public interface WorldGenerator {

	public Chunk generate(int x, int z);

}
