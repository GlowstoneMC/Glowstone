package net.lightstone.msg;

public final class PositionMessage extends Message {

	private final double x, y, stance, z;
	private final boolean flying;

	public PositionMessage(double x, double y, double stance, double z, boolean flying) {
		this.x = x;
		this.y = y;
		this.stance = stance;
		this.z = z;
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

	public boolean isFlying() {
		return flying;
	}

}
