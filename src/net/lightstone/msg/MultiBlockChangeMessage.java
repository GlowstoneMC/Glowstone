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

package net.lightstone.msg;

public final class MultiBlockChangeMessage extends Message {

	private final int chunkX, chunkZ;
	private final short[] coordinates;
	private final byte[] types, metadata;

	public MultiBlockChangeMessage(int chunkX, int chunkZ, short[] coordinates, byte[] types, byte[] metadata) {
		if (coordinates.length != types.length || types.length != metadata.length) {
			throw new IllegalArgumentException();
		}

		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.coordinates = coordinates;
		this.types = types;
		this.metadata = metadata;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public int getChanges() {
		return coordinates.length;
	}

	public short[] getCoordinates() {
		return coordinates;
	}

	public byte[] getTypes() {
		return types;
	}

	public byte[] getMetadata() {
		return metadata;
	}

}
