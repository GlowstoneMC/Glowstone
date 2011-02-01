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

import net.lightstone.msg.EntityRotationMessage;
import net.lightstone.msg.EntityTeleportMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.RelativeEntityPositionMessage;
import net.lightstone.msg.RelativeEntityPositionRotationMessage;
import net.lightstone.world.World;

public abstract class Mob extends Entity {

	public Mob(World world) {
		super(world);
	}

	@Override
	public Message createUpdateMessage() {
		boolean moved = !position.equals(previousPosition);
		boolean rotated = !rotation.equals(previousRotation);

		int x = position.getAbsX();
		int y = position.getAbsY();
		int z = position.getAbsZ();

		int dx = x - previousPosition.getAbsX();
		int dy = y - previousPosition.getAbsY();
		int dz = z - previousPosition.getAbsZ();

		boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

		int yaw = rotation.getAbsYaw();
		int pitch = rotation.getAbsPitch();

		if (moved && teleport) {
			return new EntityTeleportMessage(id, x, y, z, yaw, pitch);
		} else if (moved && rotated) {
			return new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch);
		} else if (moved) {
			return new RelativeEntityPositionMessage(id, dx, dy, dz);
		} else if (rotated) {
			return new EntityRotationMessage(id, yaw, pitch);
		}

		return null;
	}

}
