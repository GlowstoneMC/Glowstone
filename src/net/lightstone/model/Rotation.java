package net.lightstone.model;

/**
 * An immutable class which represents a rotation in the in-game world.
 * @author Graham Edgecombe
 */
public final class Rotation {

    /**
     * A rotation in which all the components are set to zero.
     */
	public static final Rotation ZERO = new Rotation(0, 0, 0);

    /**
     * The components of this rotation.
     */
	private final double yaw, pitch, roll;

    /**
     * Creates a rotation with just a yaw and pitch. The roll is set to zero.
     * @param yaw The yaw.
     * @param pitch The pitch.
     */
	public Rotation(double yaw, double pitch) {
		this(yaw, pitch, 0);
	}

    /**
     * Creates a rotation with a yaw, pitch and roll.
     * @param yaw The yaw.
     * @param pitch The pitch.
     * @param roll The roll.
     */
	public Rotation(double yaw, double pitch, int roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

    /**
     * Gets the yaw.
     * @return The yaw.
     */
	public double getYaw() {
		return yaw;
	}

    /**
     * Gets the pitch.
     * @return The pitch.
     */
	public double getPitch() {
		return pitch;
	}

    /**
     * Gets the roll.
     * @return The roll.
     */
	public double getRoll() {
		return roll;
	}

    /**
     * Gets an integer approximation of the yaw between 0 and 255.
     * @return An integer approximation of the yaw.
     */
	public int getIntYaw() {
		return (int) (((yaw % 360) / 360) * 256);
	}

    /**
     * Gets an integer approximation of the pitch between 0 and 255.
     * @return An integer approximation of the pitch.
     */
	public int getIntPitch() {
		return (int) (((pitch % 360) / 360) * 256);
	}

    /**
     * Gets an integer approximation of the roll between 0 and 255.
     * @return An integer approximation of the roll.
     */
	public int getIntRoll() {
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
