package net.glowstone.net.pipeline;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Codec.CodecRegistration;
import com.flowpowered.network.Message;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.protocol.GlowProtocol;

/**
 * Experimental pipeline component.
 */
public final class CodecsHandler extends MessageToMessageCodec<ByteBuf, Message> {

    private final GlowProtocol protocol;

    public CodecsHandler(GlowProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out)
        throws Exception {
        // find codec
        Class<? extends Message> clazz = msg.getClass();
        CodecRegistration reg = protocol.getCodecRegistration(clazz);
        if (reg == null) {
            throw new EncoderException("Unknown message type: " + clazz + ".");
        }

        // write header
        ByteBuf headerBuf = ctx.alloc().buffer(8);
        ByteBufUtils.writeVarInt(headerBuf, reg.getOpcode());

        // write body
        ByteBuf messageBuf = ctx.alloc().buffer();
        messageBuf = reg.getCodec().encode(null, messageBuf, msg);

        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
        // find codec and read header
        Codec<?> codec = protocol.newReadHeader(msg);

        // read body
        Message decoded = codec.decode(null, msg);
        if (msg.readableBytes() > 0) {
            ConsoleMessages.Warn.Net.MESSAGE_TOO_LONG.log(msg.readableBytes(), decoded);
        }

        out.add(decoded);
    }
}
