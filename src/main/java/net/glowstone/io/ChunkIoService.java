package net.glowstone.io;

import java.io.IOException;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

/**
 * This interface should be implemented by classes which wish to provide some
 * way of performing chunk I/O e.g. the {@link NbtChunkIoService}. This
 * interface is abstracted away from the implementation because a new format is
 * due to arrive soon (McRegion).
 * @author Graham Edgecombe
 */
public interface ChunkIoService {

	/**
	 * Reads a single chunk.
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @return The {@link GlowChunk} or {@code null} if it does not exist.
	 * @throws IOException if an I/O error occurs.
	 */
	public GlowChunk read(GlowWorld world, int x, int z) throws IOException;

	/**
	 * Writes a single chunk.
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param chunk The {@link GlowChunk}.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(int x, int z, GlowChunk chunk) throws IOException;

}
