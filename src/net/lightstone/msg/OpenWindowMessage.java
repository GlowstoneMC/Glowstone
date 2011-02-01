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

public final class OpenWindowMessage extends Message {

	private final int id, type, slots;
	private final String title;

	public OpenWindowMessage(int id, int type, String title, int slots) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.slots = slots;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getSlots() {
		return slots;
	}

	public String getTitle() {
		return title;
	}

}
