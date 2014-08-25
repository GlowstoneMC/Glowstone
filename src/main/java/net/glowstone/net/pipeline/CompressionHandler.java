package net.glowstone.net.pipeline;

import com.flowpowered.networking.util.ByteBufUtils;
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
public class CompressionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;

    private final int threshold;

    public CompressionHandler(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf prefixBuf = ctx.alloc().buffer(5);
        ByteBuf contentsBuf;

        if (msg.readableBytes() >= threshold) {
            // message should be compressed
            int length = msg.readableBytes();
            ByteBufUtils.writeVarInt(prefixBuf, length);

            byte[] sourceData = new byte[length];
            msg.readBytes(sourceData);

            Deflater deflater = new Deflater(COMPRESSION_LEVEL);
            deflater.setInput(sourceData);
            deflater.finish();

            byte[] compressedData = new byte[length];
            int compressedLength = deflater.deflate(compressedData);
            deflater.end();

            if (compressedLength == 0) {
                throw new EncoderException("Failed to compress message of size " + length);
            }

            contentsBuf = Unpooled.wrappedBuffer(compressedData, 0, compressedLength);
        } else {
            // message should be sent through
            ByteBufUtils.writeVarInt(prefixBuf, 0);
            contentsBuf = msg;
        }

        out.add(Unpooled.wrappedBuffer(prefixBuf, contentsBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int uncompressedSize = ByteBufUtils.readVarInt(msg);
        if (uncompressedSize == 0) {
            // message is uncompressed
            int length = msg.readableBytes();
            if (length >= threshold) {
                // invalid
                throw new DecoderException("Received uncompressed message of size " + length + " greater than threshold " + threshold);
            }

            ByteBuf buf = ctx.alloc().buffer(length);
            msg.readBytes(buf, length);
            out.add(buf);
        } else {
            // message is compressed
            byte[] sourceData = new byte[msg.readableBytes()];
            msg.readBytes(sourceData);

            Inflater inflater = new Inflater();
            inflater.setInput(sourceData);

            byte[] destData = new byte[uncompressedSize];
            int resultLength = inflater.inflate(destData);
            inflater.end();

            if (resultLength != uncompressedSize) {
                throw new DecoderException("Received compressed message claiming to be of size " + uncompressedSize + " but actually " + resultLength);
            }

            out.add(Unpooled.wrappedBuffer(destData));
        }
    }

}
