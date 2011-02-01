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

public abstract class Task {

	private int ticks, counter;
	private boolean running = true;

	public Task(int ticks) {
		this.ticks = ticks;
		this.counter = ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isStopped() {
		return !running;
	}

	public abstract void execute();

	boolean pulse() {
		if (!running)
			return false;

		if (--counter == 0) {
			counter = ticks;
			execute();
		}

		return running;
	}

}
