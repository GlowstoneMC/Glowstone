/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.glowstone.block.block2.sponge;

import com.google.common.base.Optional;

import java.util.Collection;

/**
 * Represents a basic data property for a block.
 *
 * <p>Properties are facets of {@link BlockState}s. They fundamentally allow
 * basic block types to be differentiated. For example, the direction
 * of a stairs block would be one property. More complex data, such as the
 * contents of inventories, are <strong>not</strong> considered a block
 * property, and are considered block data in Sponge.</p>
 *
 * <p>In older versions of Minecraft, block properties were encoded in
 * a block's metadata.</p>
 *
 * <p>Not all properties are serialized to disk; they may only exist only during
 * the runtime of the game, which implies that their state are initialized based
 * on their environment.</p>
 *
 * @see BlockState Contains a collection of properties
 */
public interface BlockProperty<T extends Comparable<T>> {
    /**
     * Get the name of this property.
     *
     * @return The property name
     */
    String getName();

    /**
     * Get the values that are valid for this property.
     *
     * @return A collection of valid values
     */
    Collection<T> getValidValues();

    /**
     * Get a name for the given value.
     *
     * @param value A valid value for this property
     * @return A name for the value
     */
    String getNameForValue(T value);

    /**
     * Get the value representation for the given name.
     *
     * @param name A name that represents a valid value for this property
     * @return A valid value for this property or Optional.absent() if not found
     */
    Optional<T> getValueForName(String name);

    // Subinterface markers for vanilla property types
    public interface BooleanProperty extends BlockProperty<Boolean> {

    }

    public interface EnumProperty<E extends Enum<E>> extends BlockProperty<E> {

    }

    public interface IntegerProperty extends BlockProperty<Integer> {

    }
}
