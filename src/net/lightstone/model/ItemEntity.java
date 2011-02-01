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
import net.lightstone.msg.SpawnItemMessage;
import net.lightstone.world.World;

public final class ItemEntity extends Entity {

	private final Item item;

	public ItemEntity(World world, Item item) {
		super(world);
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public Message createSpawnMessage() {
        int x = position.getIntX();
        int y = position.getIntY();
        int z = position.getIntZ();

        int yaw = rotation.getIntYaw();
        int pitch = rotation.getIntPitch();
        int roll = rotation.getIntRoll();

		return new SpawnItemMessage(id, item, x, y, z, yaw, pitch, roll);
	}

	@Override
	public Message createUpdateMessage() {
		return null;
	}

}
