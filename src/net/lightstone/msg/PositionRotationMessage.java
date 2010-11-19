package net.lightstone.msg;

public final class PositionRotationMessage extends Message {

	private final double x, y, stance, z;
	private final float rotation, pitch;
	private final boolean flying;

	public PositionRotationMessage(double x, double y, double stance, double z, float rotation, float pitch, boolean flying) {
		this.x = x;
		this.y = y;
		this.stance = stance;
		this.z = z;
		this.rotation = rotation;
		this.pitch = pitch;
		this.flying = flying;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getStance() {
		return stance;
	}

	public double getZ() {
		return z;
	}

	public float getRotation() {
		return rotation;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isFlying() {
		return flying;
	}

}
