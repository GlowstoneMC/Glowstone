package net.lightstone.model;

public final class Rotation {

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

}
