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

/**
 * Represents a single parameter - this is used for things like mob metadata.
 * @author Graham Edgecombe
 * @param <T> The type of value this parameter contains.
 */
public class Parameter<T> {

	/**
	 * The id which represents a byte parameter.
	 */
	public static final int TYPE_BYTE = 0;

	/**
	 * The id which represents a short parameter.
	 */
	public static final int TYPE_SHORT = 1;

	/**
	 * The id which represents an integer parameter.
	 */
	public static final int TYPE_INT = 2;

	/**
	 * The id which represents a float parameter.
	 */
	public static final int TYPE_FLOAT = 3;

	/**
	 * The id which represents a string parameter.
	 */
	public static final int TYPE_STRING = 4;

	/**
	 * The id which represents an item parameter.
	 */
	public static final int TYPE_ITEM = 5;

	/**
	 * The type of parameter.
	 */
	private final int type;
	
	/**
	 * The index.
	 */
	private final int index;
	
	/**
	 * The value.
	 */
	private final T value;

	/**
	 * Creates a new parameter.
	 * @param type The type of parameter.
	 * @param index The index.
	 * @param value The value.
	 */
	public Parameter(int type, int index, T value) {
		this.type = type;
		this.index = index;
		this.value = value;
	}

	/**
	 * Gets the type of the parameter.
	 * @return The type of the parameter.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the index of this parameter.
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the value of this parameter.
	 * @return The value.
	 */
	public T getValue() {
		return value;
	}

}
