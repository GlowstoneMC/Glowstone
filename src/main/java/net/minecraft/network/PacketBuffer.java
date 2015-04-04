package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

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

    public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
        return null;
    }

    public void writeNBTTagCompoundToBuffer(NBTTagCompound tag) {

    }

    public ItemStack readItemStackFromBuffer() throws IOException {
        return null;
    }

    public void writeItemStackToBuffer(ItemStack itemStack) {

    }
}
