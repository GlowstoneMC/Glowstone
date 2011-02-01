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

import net.lightstone.msg.Message;
import net.lightstone.world.World;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 * @author Graham Edgecombe
 */
public abstract class Entity {

    /**
     * The world this entity belongs to.
     */
	protected final World world;

    /**
     * A flag indicating if this entity is currently active.
     */
	protected boolean active = true;

    /**
     * This entity's unique id.
     */
	protected int id;

    /**
     * The current position.
     */
	protected Position position = Position.ZERO;

    /**
     * The position in the last cycle.
     */
	protected Position previousPosition = Position.ZERO;

    /**
     * The current rotation.
     */
	protected Rotation rotation = Rotation.ZERO;

    /**
     * The rotation in the last cycle.
     */
	protected Rotation previousRotation = Rotation.ZERO;

    /**
     * Creates an entity and adds it to the specified world.
     * @param world The world.
     */
	public Entity(World world) {
		this.world = world;
		world.getEntities().allocate(this);
	}

    /**
     * Checks if this entity is within the {@link Chunk#VISIBLE_RADIUS} of
     * another.
     * @param other The other entity.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
	public boolean isWithinDistance(Entity other) {
		double dx = Math.abs(position.getX() - other.position.getX());
		double dz = Math.abs(position.getZ() - other.position.getZ());
		return dx <= (Chunk.VISIBLE_RADIUS * Chunk.WIDTH) && dz <= (Chunk.VISIBLE_RADIUS * Chunk.HEIGHT);
	}

    /**
     * Gets the world this entity is in.
     * @return The world this entity is in.
     */
	public World getWorld() {
		return world;
	}

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
	public void destroy() {
		active = false;
		world.getEntities().deallocate(this);
	}

    /**
     * Checks if this entity is active.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean isActive() {
		return active;
	}

    /**
     * Gets the id of this entity.
     * @return The id.
     */
	public int getId() {
		return id;
	}

    /**
     * Called every game cycle. Subclasses should implement this to implement
     * periodic functionality e.g. mob AI.
     */
	public void pulse() {

	}

    /**
     * Resets the previous position and rotations of the entity to the current
     * position and rotation.
     */
	public void reset() {
		previousPosition = position;
		previousRotation = rotation;
	}

    /**
     * Gets this entity's position.
     * @return The position of this entity.
     */
	public Position getPosition() {
		return position;
	}

    /**
     * Gets the entity's previous position.
     * @return The previous position of this entity.
     */
	public Position getPreviousPosition() {
		return previousPosition;
	}

    /**
     * Sets this entity's position.
     * @param position The new position.
     */
	public void setPosition(Position position) {
		this.position = position;
	}

    /**
     * Gets this entity's rotation.
     * @return The rotation of this entity.
     */
	public Rotation getRotation() {
		return rotation;
	}

    /**
     * Gets the entity's previous rotation.
     * @return The previous rotation of this entity.
     */
	public Rotation getPreviousRotation() {
		return previousRotation;
	}

    /**
     * Sets this entity's rotation.
     * @param rotation The new rotation.
     */
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id != other.id)
			return false;
		return true;
	}

    /**
     * Creates a {@link Message} which can be sent to a client to spawn this
     * entity.
     * @return A message which can spawn this entity.
     */
	public abstract Message createSpawnMessage();

    /**
     * Creates a {@link Message} which can be sent to a client to update this
     * entity.
     * @return A message which can update this entity.
     */
	public abstract Message createUpdateMessage();

    /**
     * Checks if this entity has moved this cycle.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean hasMoved() {
		return !position.equals(previousPosition);
	}

    /**
     * Checks if this entity has rotated this cycle.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean hasRotated() {
		return !rotation.equals(previousRotation);
	}

}
