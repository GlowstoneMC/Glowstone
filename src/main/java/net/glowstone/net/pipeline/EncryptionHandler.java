package net.glowstone.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * Experimental pipeline component.
 */
public class EncryptionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final MessageHandler handler;

    private boolean enabled;

    public EncryptionHandler(MessageHandler handler) {
        this.handler = handler;
        enabled = false;
    }

    public void enable() {
        enabled = true;
        // todo
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (!enabled) {
            // encryption disabled, pass through
            msg.retain();
            out.add(msg);
            return;
        }

        throw new EncoderException("Encryption not yet implemented");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (!enabled) {
            // encryption disabled, pass through
            msg.retain();
            out.add(msg);
            return;
        }

        throw new DecoderException("Decryption not yet implemented");
    }

}
