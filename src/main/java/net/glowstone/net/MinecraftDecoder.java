package net.glowstone.net;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.MessageCodec;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

/**
 * A {@link ReplayingDecoder} which decodes {@link ChannelBuffer}s into
 * Minecraft {@link net.glowstone.msg.Message}s.
 */
public class MinecraftDecoder extends ReplayingDecoder<VoidEnum> {

    private int previousOpcode = -1;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
        GlowServer.logger.info("About to read length field");
        int length = ChannelBufferUtils.readVarInt(buf);
        int bufPosition = buf.readerIndex();
        GlowServer.logger.info("About to read opcode field (length: " + length + ")");
        int opcode = ChannelBufferUtils.readVarInt(buf);

        MessageCodec<?> codec = CodecLookupService.find(opcode);
        if (codec == null) {
            //throw new IOException("Unknown operation code: " + opcode + " (previous opcode: " + previousOpcode + ").");
            GlowServer.logger.warning("Skipping unknown opcode: " + opcode + " (previous: " + previousOpcode + ")");
            buf.readerIndex(bufPosition);
            buf.skipBytes(length);
            return null;
        }

        previousOpcode = opcode;

        GlowServer.logger.info("About to decode (opcode: " + opcode + ")");
        // safety in case codec does not read right number of bytes
        Object result = codec.decode(buf);
        if (buf.readerIndex() != bufPosition + length) {
            GlowServer.logger.warning("Opcode " + opcode + " decoded " + (buf.readerIndex() - bufPosition) + " bytes instead of " + length);
            buf.readerIndex(bufPosition + length);
        }
        return result;
    }

}
