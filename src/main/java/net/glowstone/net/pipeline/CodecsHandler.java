package net.glowstone.net.pipeline;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.protocol.Protocol;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import net.glowstone.GlowServer;
import net.glowstone.net.protocol.GlowProtocol;

import java.util.List;

/**
 * Experimental pipeline component.
 */
public class CodecsHandler extends MessageToMessageCodec<ByteBuf, Message> {

    private final MessageHandler handler;

    public CodecsHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        // find codec
        final Protocol protocol = handler.getSession().getProtocol();
        final Class<? extends Message> clazz = msg.getClass();
        Codec.CodecRegistration reg = protocol.getCodecRegistration(clazz);
        if (reg == null) {
            throw new EncoderException("Unknown message type: " + clazz + ".");
        }

        // write header
        ByteBuf headerBuf = ctx.alloc().buffer(8);
        ByteBufUtils.writeVarInt(headerBuf, reg.getOpcode());

        // write body
        ByteBuf messageBuf = ctx.alloc().buffer();
        messageBuf = reg.getCodec().encode(messageBuf, msg);

        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // find codec
        Protocol protocol = handler.getSession().getProtocol();
        // read header
        Codec<?> codec = ((GlowProtocol) protocol).newReadHeader(msg);

        // read body
        Message decoded = codec.decode(msg);
        if (msg.readableBytes() > 0) {
            GlowServer.logger.warning("Leftover bytes (" + msg.readableBytes() + ") after decoding: " + decoded);
        }

        out.add(decoded);
    }
}
