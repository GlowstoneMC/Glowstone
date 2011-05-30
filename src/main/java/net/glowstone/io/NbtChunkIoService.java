package net.glowstone.io;

import java.io.IOException;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes NBT
 * maps.
 * @author Graham Edgecombe
 */
public final class NbtChunkIoService implements ChunkIoService {

	@Override
	public GlowChunk read(GlowWorld world, int x, int z) {
		return null;
	}

	@Override
	public void write(int x, int z, GlowChunk chunk) throws IOException {

	}

}
