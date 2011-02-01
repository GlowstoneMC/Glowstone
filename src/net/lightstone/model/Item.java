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

package net.lightstone.model;

public class Item {

	private final int id, count, damage;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int count) {
		this(id, count, 0);
	}

	public Item(int id, int count, int damage) {
		this.id = id;
		this.count = count;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

}
