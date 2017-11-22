package net.glowstone.space;

/**
 * A class that contains rotation information in degrees.
 */
public class Rotator {
    private double pitch;
    private double yaw;
    private double roll;

    /**
     * Initialize a zero rotator.
     */
    public Rotator() {
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
    }

    /**
     * Initialize all components (pitch, yaw and roll) to this angle.
     *
     * @param angle The angle in degrees
     */
    public Rotator(double angle) {
        this.pitch = angle;
        this.yaw = angle;
        this.roll = angle;
    }

    /**
     * Initialize a Rotator with a specific pitch, yaw and roll.
     *
     * @param pitch The pitch in degrees
     * @param yaw The yaw in degrees
     * @param roll The roll in degrees
     */
    public Rotator(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Initialize a Rotator from a Quaterion
     *
     * @param quat The quaterion
     */
    public Rotator(Quaterion quat) {

    }

    public Rotator add(Rotator rot) {
        pitch += rot.pitch;
        yaw += rot.yaw;
        roll += rot.roll;
        return this;
    }

    public Rotator subtract(Rotator rot) {
        pitch -= rot.pitch;
        yaw -= rot.yaw;
        roll -= rot.roll;
        return this;
    }

    public Rotator multiply(double scale) {
        pitch *= scale;
        yaw *= scale;
        roll *= scale;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Rotator) {
            Rotator rot = (Rotator) other;
            return pitch == rot.pitch && yaw == rot.yaw && roll == rot.roll;
        }
        return false;
    }

    public boolean isNearlyZero() {
        return isNearlyZero(0.00001);
    }

    public boolean isNearlyZero(double tolerance) {
        return pitch <= tolerance && yaw <= tolerance && roll <= tolerance;
    }

    public boolean isZero() {
        return pitch == 0 && yaw == 0 && roll == 0;
    }
}
