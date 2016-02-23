package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public class PacketBuffer extends ByteBuf {

    public PacketBuffer() {

    }

    @Override
    public int capacity() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf capacity(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int maxCapacity() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteOrder order() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf order(ByteOrder byteOrder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf unwrap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDirect() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int readerIndex() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int writerIndex() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setIndex(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PacketBuffer(ByteBuf buf) {

    }

    public byte[] array() {
        return new byte[] { };
    }

    @Override
    public int arrayOffset() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long memoryAddress() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString(Charset charset) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString(int i, int i2, Charset charset) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int hashCode() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object o) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(ByteBuf byteBuf) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf retain(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean release() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean release(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int refCnt() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf retain() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeString(String data) {
    }

    public ByteBuf writeByte(int data) {
        return null;
    }

    @Override
    public ByteBuf writeShort(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeMedium(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ByteBuf writeInt(int data) {
        return null;
    }

    @Override
    public ByteBuf writeLong(long l) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeChar(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeFloat(float v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeDouble(double v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes, int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeZero(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int indexOf(int i, int i2, byte b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int bytesBefore(byte b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int bytesBefore(int i, byte b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int bytesBefore(int i, int i2, byte b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int forEachByte(ByteBufProcessor byteBufProcessor) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int forEachByte(int i, int i2, ByteBufProcessor byteBufProcessor) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor byteBufProcessor) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int forEachByteDesc(int i, int i2, ByteBufProcessor byteBufProcessor) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf copy() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf copy(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf slice() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf slice(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf duplicate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int nioBufferCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuffer nioBuffer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuffer nioBuffer(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int i2) {
        return new ByteBuffer[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasArray() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String readStringFromBuffer(int length) {
        return null;
    }

    public short readUnsignedByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int readUnsignedShort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int readMedium() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int readUnsignedMedium() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int readInt() {
        return 0;
    }

    @Override
    public long readUnsignedInt() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long readLong() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public char readChar() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float readFloat() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double readDouble() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readSlice(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte readByte() {
        return 0;
    }

    public int readableBytes() {
        return 0;
    }

    @Override
    public int writableBytes() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int maxWritableBytes() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReadable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReadable(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWritable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWritable(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf clear() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf markReaderIndex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf markWriterIndex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf discardReadBytes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int ensureWritable(int i, boolean b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBoolean(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByte(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short getUnsignedByte(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short getShort(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getUnsignedShort(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMedium(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getUnsignedMedium(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInt(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getUnsignedInt(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLong(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public char getChar(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getFloat(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getDouble(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i2, int i3) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes, int i2, int i3) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputStream, int i2) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int i2) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBoolean(int i, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setByte(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setShort(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setMedium(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setInt(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setLong(int i, long l) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setChar(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setFloat(int i, float v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setDouble(int i, double v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i2, int i3) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes, int i2, int i3) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int setBytes(int i, InputStream inputStream, int i2) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int i2) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf setZero(int i, int i2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean readBoolean() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ByteBuf readBytes(byte[] bytes, int offset, int length) {
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ByteBuf writeBoolean(boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
