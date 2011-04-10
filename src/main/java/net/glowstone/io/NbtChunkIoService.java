package net.glowstone.io;

import java.io.IOException;

import net.glowstone.model.Chunk;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes NBT
 * maps.
 * @author Graham Edgecombe
 */
public final class NbtChunkIoService implements ChunkIoService {

	@Override
	public Chunk read(int x, int z) {
		return null;
	}

	@Override
	public void write(int x, int z, Chunk chunk) throws IOException {

	}

}
