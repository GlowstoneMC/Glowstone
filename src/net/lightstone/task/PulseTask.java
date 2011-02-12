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

package net.lightstone.task;

import net.lightstone.Server;
import net.lightstone.net.SessionRegistry;
import net.lightstone.world.World;

/**
 * A simple {@link Task} which calls the {@link SessionRegistry#pulse()} and
 * {@link World#pulse()} methods every tick.
 * @author Graham Edgecombe
 */
public final class PulseTask extends Task {

	/**
	 * The server.
	 */
	private final Server server;

	/**
	 * Creates a new pulse task.
	 * @param server The server.
	 */
	public PulseTask(Server server) {
		super(1);
		this.server = server;
	}

	@Override
	public void execute() {
		server.getSessionRegistry().pulse();
		server.getWorld().pulse();
	}

}
