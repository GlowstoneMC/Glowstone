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

public final class IdentificationMessage extends Message {

	private final int id, dimension;
	private final String name, message;
	private final long seed;

	public IdentificationMessage(int id, String name, String message, long seed, int dimension) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.seed = seed;
		this.dimension = dimension;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public long getSeed() {
		return seed;
	}

	public int getDimension() {
		return dimension;
	}

}
