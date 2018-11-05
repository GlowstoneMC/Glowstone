package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import net.glowstone.net.message.play.inv.WindowPropertyMessage;

public final class WindowPropertyCodec implements Codec<WindowPropertyMessage> {

    @Override
    public WindowPropertyMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode WindowPropertyMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, WindowPropertyMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getProperty());
        buf.writeShort(message.getValue());
        return buf;
    }
}
