package net.glowstone.net.pipeline;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
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
public final class CodecsHandler extends MessageToMessageCodec<ByteBuf, Message> {

    private final GlowProtocol protocol;

    public CodecsHandler(GlowProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        // find codec
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
        // find codec and read header
        final Codec<?> codec = protocol.newReadHeader(msg);

        // read body
        Message decoded = codec.decode(msg);
        if (msg.readableBytes() > 0) {
            GlowServer.logger.warning("Leftover bytes (" + msg.readableBytes() + ") after decoding: " + decoded);
        }

        out.add(decoded);
    }
}
