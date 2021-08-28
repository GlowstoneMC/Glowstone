package net.glowstone.net.pipeline;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Experimental pipeline component.
 */
public final class CompressionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;

    private final int threshold;
    private final Inflater inflater;
    private final Deflater deflater;

    /**
     * Creates an instance that compresses messages using an {@link Inflater} and {@link Deflater}.
     *
     * @param threshold the smallest message length, in bytes, to compress
     */
    public CompressionHandler(int threshold) {
        this.threshold = threshold;
        inflater = new Inflater();
        deflater = new Deflater(COMPRESSION_LEVEL);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
        ByteBuf prefixBuf = ctx.alloc().buffer(5);
        ByteBuf contentsBuf;

        int length = msg.readableBytes();
        if (length >= threshold) {
            // message should be compressed
            int index = msg.readerIndex();

            ByteBuf sourceData = ctx.alloc().heapBuffer(length);
            msg.readBytes(sourceData);
            deflater.setInput(sourceData.array(), sourceData.arrayOffset() + sourceData.readerIndex(), length);
            deflater.finish();

            ByteBuf compressedData = ctx.alloc().heapBuffer(length);
            int compressedLength = deflater.deflate(
                    compressedData.array(),
                    compressedData.arrayOffset() + compressedData.writerIndex(),
                    compressedData.writableBytes(), Deflater.SYNC_FLUSH);

            deflater.reset();
            sourceData.release();

            if (compressedLength == 0) {
                // compression failed in some weird way
                compressedData.release();
                throw new EncoderException("Failed to compress message of size " + length);
            } else if (compressedLength >= length) {
                // compression increased the size. threshold is probably too low
                // send as an uncompressed packet
                compressedData.release();
                ByteBufUtils.writeVarInt(prefixBuf, 0);
                msg.readerIndex(index);
                msg.retain();
                contentsBuf = msg;
            } else {
                // all is well
                ByteBufUtils.writeVarInt(prefixBuf, length);
                contentsBuf = Unpooled.wrappedBuffer(
                        compressedData.array(),
                        compressedData.arrayOffset() + compressedData.readerIndex(),
                        compressedLength);
                compressedData.release();
            }
        } else {
            // message should be sent through
            ByteBufUtils.writeVarInt(prefixBuf, 0);
            msg.retain();
            contentsBuf = msg;
        }

        out.add(Unpooled.wrappedBuffer(prefixBuf, contentsBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
        int index = msg.readerIndex();
        int uncompressedSize = ByteBufUtils.readVarInt(msg);
        if (uncompressedSize == 0) {
            // message is uncompressed
            int length = msg.readableBytes();
            if (length >= threshold) {
                // invalid
                throw new DecoderException(
                    "Received uncompressed message of size " + length + " greater than threshold "
                        + threshold);
            }

            ByteBuf buf = ctx.alloc().buffer(length);
            msg.readBytes(buf, length);
            out.add(buf);
        } else {
            // message is compressed
            byte[] sourceData = new byte[msg.readableBytes()];
            msg.readBytes(sourceData);
            inflater.setInput(sourceData);

            byte[] destData = new byte[uncompressedSize];
            int resultLength = inflater.inflate(destData);
            inflater.reset();

            if (resultLength == 0) {
                // might be a leftover from before compression was enabled (no compression header)
                // uncompressedSize is likely to be < threshold
                msg.readerIndex(index);
                msg.retain();
                out.add(msg);
            } else if (resultLength != uncompressedSize) {
                throw new DecoderException(
                    "Received compressed message claiming to be of size " + uncompressedSize
                        + " but actually " + resultLength);
            } else {
                out.add(Unpooled.wrappedBuffer(destData));
            }
        }
    }

}
