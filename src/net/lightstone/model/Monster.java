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

import java.util.ArrayList;
import java.util.List;

import net.lightstone.msg.Message;
import net.lightstone.msg.SpawnMobMessage;
import net.lightstone.util.Parameter;
import net.lightstone.world.World;

/**
 * Represents a monster such as a creeper.
 * @author Graham Edgecombe
 */
public final class Monster extends Mob {

    /**
     * The type of monster.
     */
	private final int type;

    /**
     * The monster's metadata.
     */
	private final List<Parameter<?>> metadata = new ArrayList<Parameter<?>>();

    /**
     * Creates a new monster.
     * @param world The world this monster is in.
     * @param type The type of monster.
     */
	public Monster(World world, int type) {
		super(world);
		this.type = type;
	}

    /**
     * Gets the type of monster.
     * @return The type of monster.
     */
	public int getType() {
		return type;
	}

	@Override
	public Message createSpawnMessage() {
		int x = position.getIntX();
		int y = position.getIntY();
		int z = position.getIntZ();
		int yaw = rotation.getIntYaw();
		int pitch = rotation.getIntPitch();
		return new SpawnMobMessage(id, type, x, y, z, yaw, pitch, metadata);
	}

}
