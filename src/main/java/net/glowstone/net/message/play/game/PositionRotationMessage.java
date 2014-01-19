package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import org.bukkit.Location;

public final class PositionRotationMessage implements Message {

    private final double x, y, z;
    private final float rotation, pitch;
    private final boolean onGround;

    public PositionRotationMessage(double x, double y, double z, float rotation, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PositionRotationMessage(Location location, boolean onGround) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), onGround);
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

    public float getRotation() {
        return rotation;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public String toString() {
        return "PositionRotationMessage{x=" + x + ",y=" + y + ",z=" + z +
                ",rotation=" + rotation + ",pitch=" +
                pitch + ",onGround=" + onGround + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
