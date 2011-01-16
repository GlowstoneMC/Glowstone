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
