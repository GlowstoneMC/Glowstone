package net.minecraft.network;

import io.netty.buffer.ByteBuf;

public class PacketBuffer {

    public PacketBuffer() {

    }

    public PacketBuffer(ByteBuf buf) {

    }

    public byte[] array() {
        return new byte[] { };
    }

    public void writeString(String data) {
    }

    public void writeByte(int data) {
    }

    public void writeInt(int data) {
    }

    public String readStringFromBuffer(int length) {
        return null;
    }

    public int readUnsignedByte() {
        return 0;
    }

    public int readInt() {
        return 0;
    }

    public byte readByte() {
        return 0;
    }

    public int readableBytes() {
        return 0;
    }

    public void readBytes(byte[] bytes, int offset, int length) {

    }
}
