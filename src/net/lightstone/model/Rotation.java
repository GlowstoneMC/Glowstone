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

public final class Rotation {

	public static final Rotation ZERO = new Rotation(0, 0, 0);

	private final double yaw, pitch, roll;

	public Rotation(double yaw, double pitch) {
		this(yaw, pitch, 0);
	}

	public Rotation(double yaw, double pitch, int roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public double getRoll() {
		return roll;
	}

	public int getAbsYaw() {
		return (int) (((yaw % 360) / 360) * 256);
	}

	public int getAbsPitch() {
		return (int) (((pitch % 360) / 360) * 256);
	}

	public int getAbsRoll() {
		return (int) (((roll % 360) / 360) * 256);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(pitch);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(roll);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yaw);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Rotation other = (Rotation) obj;
		if (Double.doubleToLongBits(pitch) != Double
				.doubleToLongBits(other.pitch))
			return false;
		if (Double.doubleToLongBits(roll) != Double
				.doubleToLongBits(other.roll))
			return false;
		if (Double.doubleToLongBits(yaw) != Double.doubleToLongBits(other.yaw))
			return false;
		return true;
	}

}
