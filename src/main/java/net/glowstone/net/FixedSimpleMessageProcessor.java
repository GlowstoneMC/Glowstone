package net.glowstone.net;

import com.flowpowered.networking.processor.MessageProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Temporary class until Flow is fixed.
 */
public abstract class FixedSimpleMessageProcessor implements MessageProcessor {
    protected final int capacity;
    private final byte[] decodingByteBuffer;
    private final byte[] encodingByteBuffer;

    public FixedSimpleMessageProcessor(int capacity) {
        this.capacity = capacity;
        this.decodingByteBuffer = new byte[capacity];
        this.encodingByteBuffer = new byte[capacity];
    }

    @Override
    public final synchronized void processEncode(ChannelHandlerContext ctx, final ByteBuf input, ByteBuf buffer) {
        int remaining;
        while ((remaining = input.readableBytes()) > 0) {
            int clamped = Math.min(remaining, capacity);
            input.readBytes(encodingByteBuffer, 0, clamped);
            writeEncode(encodingByteBuffer, clamped);
            int read;
            while ((read = readEncode(encodingByteBuffer)) > 0) {  // CHANGED HERE
                buffer.writeBytes(encodingByteBuffer, 0, read);
            }
        }
    }

    /**
     * Writes data to the processor<br> <br> This method does not need to be thread safe
     *
     * @param buf a buffer containing the data
     * @param length the length of the data to process
     */
    protected abstract void writeEncode(byte[] buf, int length);

    /**
     * Reads the data from the processor into the given array<br> <br> This method does not need to be thread safe
     *
     * @param buf the byte array to process the data to
     * @return the number of bytes written
     */
    protected abstract int readEncode(byte[] buf);

    @Override
    public final synchronized void processDecode(ChannelHandlerContext ctx, final ByteBuf input, ByteBuf buffer) {
        int remaining;
        while ((remaining = input.readableBytes()) > 0) {
            int clamped = Math.min(remaining, capacity);
            input.readBytes(decodingByteBuffer, 0, clamped);
            writeDecode(decodingByteBuffer, clamped);
            int read;
            while ((read = readDecode(decodingByteBuffer)) > 0) {
                buffer.writeBytes(decodingByteBuffer, 0, read);
            }
        }
    }

    /**
     * Writes data to the processor<br> <br> This method does not need to be thread safe
     *
     * @param buf a buffer containing the data
     * @param length the length of the data to process
     */
    protected abstract void writeDecode(byte[] buf, int length);

    /**
     * Reads the data from the processor into the given array<br> <br> This method does not need to be thread safe
     *
     * @param buf the byte array to process the data to
     * @return the number of bytes written
     */
    protected abstract int readDecode(byte[] buf);
}
