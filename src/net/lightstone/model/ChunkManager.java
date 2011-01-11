package net.lightstone.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.lightstone.io.ChunkIoService;
import net.lightstone.world.WorldGenerator;

public final class ChunkManager {

	private final ChunkIoService service;
	private final WorldGenerator generator;
	private final Map<Chunk.Key, Chunk> chunks = new HashMap<Chunk.Key, Chunk>();

	public ChunkManager(ChunkIoService service, WorldGenerator generator) {
		this.service = service;
		this.generator = generator;
	}

	public Chunk getChunk(int x, int z) {
		Chunk.Key key = new Chunk.Key(x, z);
		Chunk chunk = chunks.get(key);
		if (chunk == null) {
			try {
				chunk = service.read(x, z);
			} catch (IOException e) {
				chunk = null;
			}

			if (chunk == null) {
				chunk = generator.generate(x, z);
			}

			chunks.put(key, chunk);
		}
		return chunk;
	}

}
