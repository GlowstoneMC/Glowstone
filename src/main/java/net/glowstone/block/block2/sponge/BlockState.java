/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.spongepowered.api.block;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.ImmutableDataBuilder;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.property.DirectionRelativePropertyHolder;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a particular "state" that can exist at a {@link Location} with
 * a particular {@link BlockType} and various {@link ImmutableValue}s defining
 * the information for the "block". Note that normally, there may exist only
 * a single instance of a particular {@link BlockState} as they are immutable,
 * a particular instance may be cached for various uses.
 */
public interface BlockState {

    /**
     * Gets the base type of block.
     *
     * <p>The type does not include block data such as the contents of
     * inventories.</p>
     *
     * @return The type of block
     */
    BlockType getType();

    /**
     * Applies extended properties for the current @{link BlockType} if any to
     * the current {@link BlockState}. This usually is gathered from surrounding
     * {@link BlockState}'s.
     *
     * <p>Note: This should only be called for live {@link BlockState}'s at
     * a specific {@link Location} for accurate results.</p>
     *
     * <p>
     * Examples of some extended properties are:
     * </p>
     *
     * <ul>
     *     <li>snow on podzul dirt block</li>
     *     <li>occupied status for beds</li>
     *     <li>fence connections</li>
     * </ul>
     *
     * @param location The location used to search for extended properties
     * @return The blockstate with extended properties included if any
     */
    BlockState withExtendedProperties(Location location);

    /**
     * Gets the associated {@link BlockState} with the cycled
     * {@link BaseValue}. Note that only {@link Cycleable} values can be
     * cycled. To change a particular {@link Key}'ed {@link Value}, usage
     * of the {@link BlockState#with(Key, Object)} is recommended.
     *
     * @param key The key to cycle
     * @return The blockstate instance with the cycled value
     */
    BlockState cycleValue(BlockTrait<?> trait);

    /**
     * Creates a new {@link BlockSnapshot} with this current {@link BlockState}
     * at the desired {@link Location}. If the {@link Location} has the same
     * {@link BlockState}, and the {@link BlockType} can house a
     * {@link TileEntity}, the data from the tile entity may be included in the
     * returned  {@link BlockSnapshot}.
     *
     * @param location The location for the snapshot
     * @return The newly created snapshot
     */
    BlockSnapshot snapshotFor(Location location);

    /**
     * Gets the {@link Comparable} value for the specific {@link BlockTrait}
     * such that if the {@link BlockState} does not support the
     * {@link BlockTrait}, {@link Optional#empty()} is returned.
     *
     * @param blockTrait The block trait instance
     * @param <T> The generic type of block trait
     * @return The comparable value, if available and compatible
     */
    <T extends Comparable<T>> Optional<T> getTraitValue(BlockTrait<T> blockTrait);

    /**
     * Attempts to retrieve the {@link BlockTrait} instance associated with
     * this {@link BlockState}s {@link BlockType} by string id. If there is no
     * {@link BlockTrait} available, {@link Optional#empty()} is returned.
     *
     * @param blockTrait The block trait id
     * @return The block trait, if available
     */
    Optional<BlockTrait<?>> getTrait(String blockTrait);

    /**
     * Gets the {@link BlockState} with the appropriate value for the given
     * {@link BlockTrait}. If the {@link BlockTrait} is not supported,
     * {@link Optional#empty()} is returned. If the object is not either
     * an instance contained in {@link BlockTrait#getPossibleValues()} or
     * an instance {@link Object#toString()}, {@link Optional#empty()} may be
     * returned.
     *
     * @param trait The trait
     * @param value The value
     * @return The blockstate, if supported
     */
    Optional<BlockState> withTrait(BlockTrait<?> trait, Object value);

    /**
     * Gets an immutable {@link Collection} of all applicable
     * {@link BlockTrait}s for this {@link BlockState}.
     *
     * @return An immutable collection of all applicable block traits
     */
    Collection<BlockTrait<?>> getTraits();

    /**
     * Gets an immutable {@link Collection} of all the values for all
     * {@link BlockTrait}s for this {@link BlockState}.
     *
     * @return An immutable collection of all the values for all applicable
     *     traits
     */
    Collection<?> getTraitValues();

    /**
     * Gets an immutable or unmodifiable {@link Map} of the known {@link BlockTrait}s
     * to their current values for this {@link BlockState}.
     *
     * @return The immutable map of block traits to their values representing
     *     this block state
     */
    Map<BlockTrait<?>, ?> getTraitMap();
}