package net.lightstone.io;

import java.io.IOException;

import net.lightstone.model.Chunk;

public interface ChunkIoService {

	public Chunk read(int x, int z) throws IOException;

	public void write(int x, int z, Chunk chunk) throws IOException;

}
