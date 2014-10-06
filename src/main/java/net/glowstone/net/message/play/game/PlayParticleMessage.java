package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Arrays;

public final class PlayParticleMessage implements Message {

    private final int particle;
    private final boolean longDistance;
    private final float x, y, z;
    private final float ofsX, ofsY, ofsZ;
    private final float data;
    private final int count;
    private final int[] extData;

    public PlayParticleMessage(int particle, boolean longDistance, float x, float y, float z, float ofsX, float ofsY, float ofsZ, float data, int count, int... extData) {
        this.particle = particle;
        this.longDistance = longDistance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ofsX = ofsX;
        this.ofsY = ofsY;
        this.ofsZ = ofsZ;
        this.data = data;
        this.count = count;
        this.extData = extData;
    }

    public int getParticle() {
        return particle;
    }

    public boolean getLongDistance() {
        return longDistance;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getOfsX() {
        return ofsX;
    }

    public float getOfsY() {
        return ofsY;
    }

    public float getOfsZ() {
        return ofsZ;
    }

    public float getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public int[] getExtData() {
        return extData;
    }

    @Override
    public String toString() {
        return "PlayParticleMessage{" +
                "particle=" + particle +
                ", longDistance=" + longDistance +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", ofsX=" + ofsX +
                ", ofsY=" + ofsY +
                ", ofsZ=" + ofsZ +
                ", data=" + data +
                ", count=" + count +
                ", extData=" + Arrays.toString(extData) +
                '}';
    }
}

