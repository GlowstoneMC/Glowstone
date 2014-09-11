package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.inv.WindowPropertyMessage;

import java.io.IOException;

public final class WindowPropertyCodec implements Codec<WindowPropertyMessage> {
    @Override
    public WindowPropertyMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode WindowPropertyMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WindowPropertyMessage message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getProperty());
        buf.writeShort(message.getValue());
        return buf;
    }
}
