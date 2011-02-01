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

package net.lightstone.util;

public class Parameter<T> {

	public static final int TYPE_BYTE = 0;

	public static final int TYPE_SHORT = 1;

	public static final int TYPE_INT = 2;

	public static final int TYPE_FLOAT = 3;

	public static final int TYPE_STRING = 4;

	public static final int TYPE_ITEM = 5;

	private final int type, index;
	private final T value;

	public Parameter(int type, int index, T value) {
		this.type = type;
		this.index = index;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public T getValue() {
		return value;
	}

}
