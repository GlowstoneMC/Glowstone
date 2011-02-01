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

public final class PlayNoteMessage extends Message {

	private final int x, y, z, instrument, pitch;

	public PlayNoteMessage(int x, int y, int z, int instrument, int pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.instrument = instrument;
		this.pitch = pitch;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getInstrument() {
		return instrument;
	}

	public int getPitch() {
		return pitch;
	}

}
