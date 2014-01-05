package net.glowstone.net;

import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * A {@link FrameDecoder} which decodes {@link ChannelBuffer}s into
 * Minecraft {@link net.glowstone.net.message.Message}s.
 */
public class MinecraftDecoder extends FrameDecoder {

    private final MinecraftHandler handler;

    public MinecraftDecoder(MinecraftHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 2) {
            // a packet length + opcode is always at least 2 bytes
            return null;
        }

        buffer.markReaderIndex();

        // see if an entire varint is available
        short read;
        do {
            if (buffer.readableBytes() < 1) {
                buffer.resetReaderIndex();
                return null;
            }
            read = buffer.readUnsignedByte();
        } while (((read >> 7) & 1) != 0);

        // read and check length
        buffer.resetReaderIndex();
        int length = ChannelBufferUtils.readVarInt(buffer);
        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return null;
        }

        // decode message
        ChannelBuffer frame = buffer.readBytes(length);
        Session session = handler.session; // hacky, fix later
        MessageMap map = MessageMap.getForState(session.getState());
        return map.decode(frame);
    }

}
