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

public abstract class Entity {

	protected final World world;

	protected boolean active = true;

	protected int id;

	protected Position position = Position.ZERO;

	protected Position previousPosition = Position.ZERO;

	protected Rotation rotation = Rotation.ZERO;

	protected Rotation previousRotation = Rotation.ZERO;

	public Entity(World world) {
		this.world = world;
		world.getEntities().allocate(this);
	}

	public boolean isWithinDistance(Entity other) {
		double dx = Math.abs(position.getX() - other.position.getX());
		double dz = Math.abs(position.getZ() - other.position.getZ());
		return dx <= (Chunk.VISIBLE_RADIUS * Chunk.WIDTH) && dz <= (Chunk.VISIBLE_RADIUS * Chunk.HEIGHT);
	}

	public World getWorld() {
		return world;
	}

	public void destroy() {
		active = false;
		world.getEntities().deallocate(this);
	}

	public boolean isActive() {
		return active;
	}

	public int getId() {
		return id;
	}

	public void pulse() {

	}

	public void reset() {
		previousPosition = position;
		previousRotation = rotation;
	}

	public Position getPosition() {
		return position;
	}

	public Position getPreviousPosition() {
		return previousPosition;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public Rotation getPreviousRotation() {
		return previousRotation;
	}

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

	public abstract Message createSpawnMessage();

	public abstract Message createUpdateMessage();

	public boolean hasMoved() {
		return !position.equals(previousPosition);
	}

	public boolean hasRotated() {
		return !rotation.equals(previousRotation);
	}

}
