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

/**
 * A class which represents an {@link Item} and its associated slot in an
 * inventory.
 */
public final class SlottedItem {

    /**
     * The slot.
     */
	private final int slot;

    /**
     * The item.
     */
	private final Item item;

    /**
     * Creates a slotted item.
     * @param slot The slot.
     * @param item The item.
     */
	public SlottedItem(int slot, Item item) {
		this.slot = slot;
		this.item = item;
	}

    /**
     * Gets the slot.
     * @return The slot.
     */
	public int getSlot() {
		return slot;
	}

    /**
     * Gets the item.
     * @return The item.
     */
	public Item getItem() {
		return item;
	}

}
