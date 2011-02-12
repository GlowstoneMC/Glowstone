/*
 * Copyright (c) 2010-2011 Graham Edgecombe.
 *
 * This file is part of Lightstone.
 *
 * Lightstone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Lightstone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightstone.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.lightstone.io;

import java.io.IOException;

import net.lightstone.model.Chunk;

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
	 * @return The {@link Chunk} or {@code null} if it does not exist.
	 * @throws IOException if an I/O error occurs.
	 */
	public Chunk read(int x, int z) throws IOException;

	/**
	 * Writes a single chunk.
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param chunk The {@link Chunk}.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(int x, int z, Chunk chunk) throws IOException;

}
