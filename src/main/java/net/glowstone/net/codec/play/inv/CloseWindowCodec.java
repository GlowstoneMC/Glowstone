package net.glowstone.net.codec.play.inv;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.CloseWindowMessage;

import java.io.IOException;

public final class CloseWindowCodec implements Codec<CloseWindowMessage> {
    @Override
    public CloseWindowMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readUnsignedByte();
        return new CloseWindowMessage(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CloseWindowMessage message) throws IOException {
        buf.writeByte(message.getId());
        return buf;
    }
}
