package net.glowstone.msg;

import java.util.Arrays;

public final class ExplosionMessage extends Message {

    private final double x, y, z;
    private final float radius;
    private final byte[] coordinates;

    public ExplosionMessage(double x, double y, double z, float radius, byte[] coordinates) {
        if (coordinates.length % 3 != 0) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.coordinates = coordinates;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getRadius() {
        return radius;
    }

    public int getRecords() {
        return coordinates.length / 3;
    }

    public byte[] getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "ExplosionMessage{x=" + x + ",y=" + y + ",z=" + z + ",radius=" + radius + ",coordinates=" + Arrays.toString(coordinates) + "}";
    }
}
