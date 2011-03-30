package net.glowstone.model;

/**
 * An immutable class which represents a position within the in-game world.
 * @author Graham Edgecombe
 */
public final class Position {

    /**
     * The number of integer values between each double value. For example, if
     * the coordinate was {@code 1.5}, this would be sent as
     * {@code 1.5 * 32 = 48} within certain packets.
     */
	public static final int GRANULARITY = 32;

    /**
     * A position where all the coordinates are set to zero.
     */
	public static final Position ZERO = new Position(0, 0, 0);

    /**
     * The coordinates.
     */
	private final double x, y, z;

    /**
     * Creates a new position.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     */
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

    /**
     * Gets the X coordinate.
     * @return The X coordinate.
     */
	public double getX() {
		return x;
	}

    /**
     * Gets the Y coordinate.
     * @return The Y coordinate.
     */
	public double getY() {
		return y;
	}

    /**
     * Gets the Z coordinate.
     * @return The Z coordinate.
     */
	public double getZ() {
		return z;
	}

    /**
     * Gets the X coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the X coordinate.
     */
	public int getIntX() {
		return (int) (x * GRANULARITY);
	}

    /**
     * Gets the Y coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the Y coordinate.
     */
	public int getIntY() {
		return (int) (y * GRANULARITY);
	}

    /**
     * Gets the Z coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the Z coordinate.
     */
	public int getIntZ() {
		return (int) (z * GRANULARITY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
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
		Position other = (Position) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

}
