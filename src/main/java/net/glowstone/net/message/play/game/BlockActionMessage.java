package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class BlockActionMessage implements Message {

    private final int x, y, z, data1, data2, blockType;

    public BlockActionMessage(int x, int y, int z, int data1, int data2, int blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.data1 = data1;
        this.data2 = data2;
        this.blockType = blockType;
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

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public int getBlockType() {
        return blockType;
    }

    @Override
    public String toString() {
        return "BlockActionMessage{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", data1=" + data1 +
                ", data2=" + data2 +
                ", blockType=" + blockType +
                '}';
    }
}
