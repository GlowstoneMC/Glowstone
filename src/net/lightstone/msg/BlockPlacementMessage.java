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

public final class BlockPlacementMessage extends Message {

	private final int id, x, y, z, direction, count, damage;

	public BlockPlacementMessage(int x, int y, int z, int direction) {
		this(x, y, z, direction, -1, 0, 0);
	}

	public BlockPlacementMessage(int x, int y, int z, int direction, int id, int count, int damage) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
		this.count = count;
		this.damage = damage;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

	public int getId() {
		return id;
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

	public int getDirection() {
		return direction;
	}

}
