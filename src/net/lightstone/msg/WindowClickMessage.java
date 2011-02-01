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

public final class WindowClickMessage extends Message {

	private final int id, slot;
	private final boolean rightClick;
	private final int transaction, item, count, damage;

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction) {
		this(id, slot, rightClick, transaction, -1, 0, 0);
	}

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, int item, int count, int damage) {
		this.id = id;
		this.slot = slot;
		this.rightClick = rightClick;
		this.transaction = transaction;
		this.item = item;
		this.count = count;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isRightClick() {
		return rightClick;
	}

	public int getTransaction() {
		return transaction;
	}

	public int getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

}
