package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.inv.WindowPropertyPacket;

import java.io.IOException;

public final class WindowPropertyCodec implements Codec<WindowPropertyPacket> {
    @Override
    public WindowPropertyPacket decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode WindowPropertyMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WindowPropertyPacket message) throws IOException {
        buf.writeByte(message.getId());
        buf.writeShort(message.getProperty());
        buf.writeShort(message.getValue());
        return buf;
    }
}
