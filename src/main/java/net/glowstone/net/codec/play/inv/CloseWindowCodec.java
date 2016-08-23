package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.WindowClosePacket;

import java.io.IOException;

public final class CloseWindowCodec implements Codec<WindowClosePacket> {
    @Override
    public WindowClosePacket decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        return new WindowClosePacket(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WindowClosePacket message) throws IOException {
        buf.writeByte(message.getId());
        return buf;
    }
}
