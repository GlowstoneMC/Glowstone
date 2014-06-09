package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class PlaySoundMessage implements Message {

    private final String sound;
    private final double x, y, z;
    private final float volume, pitch;

    public PlaySoundMessage(String sound, double x, double y, double z, float volume, float pitch) {
        this.sound = sound;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getSound() {
        return sound;
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

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return "PlaySoundMessage{" +
                "sound='" + sound + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}

