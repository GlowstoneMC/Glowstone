package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class SpawnObjectMessage implements Message {

    public static final int ITEM = 2;

    private final int id, type, x, y, z, pitch, yaw, data, velX, velY, velZ;

    public SpawnObjectMessage(int id, int type, int x, int y, int z, int pitch, int yaw) {
        this(id, type, x, y, z, pitch, yaw, 0, 0, 0, 0);
    }
    public SpawnObjectMessage(int id, int type, int x, int y, int z, int pitch, int yaw, int data, int velX, int velY, int velZ) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.data = data;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
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

    public int getPitch() {
        return pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public boolean hasFireball() {
        return data != 0;
    }

    public int getData() {
        return data;
    }

    public int getVelX() {
        return velX;
    }

    public int getVelY() {
        return velY;
    }

    public int getVelZ() {
        return velZ;
    }

    @Override
    public String toString() {
        return "SpawnObjectMessage{" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                ", data=" + data +
                ", velX=" + velX +
                ", velY=" + velY +
                ", velZ=" + velZ +
                '}';
    }
}
