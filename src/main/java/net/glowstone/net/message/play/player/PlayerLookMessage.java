package net.glowstone.net.message.play.player;

import org.bukkit.Location;

public final class PlayerLookMessage extends PlayerUpdateMessage {

    private final float yaw, pitch;

    public PlayerLookMessage(float yaw, float pitch, boolean onGround) {
        super(onGround);
        this.yaw = (yaw % 360 + 360) % 360;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void update(Location location) {
        location.setYaw(yaw);
        location.setPitch(pitch);
    }

    @Override
    public String toString() {
        return "PlayerLookMessage{" +
                "onGround=" + getOnGround() +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
