package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class PlayEffectMessage implements Message {

    private final int id;
    private final int x, y, z;
    private final int data;
    private final boolean ignoreDistance;

    public PlayEffectMessage(int id, int x, int y, int z, int data, boolean ignoreDistance) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
        this.ignoreDistance = ignoreDistance;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public int getData() {
        return data;
    }

    public boolean getIgnoreDistance() {
        return ignoreDistance;
    }

    @Override
    public String toString() {
        return "PlayEffectMessage{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", data=" + data +
                ", ignoreDistance=" + ignoreDistance +
                '}';
    }
}
