package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.inv.CloseWindowMessage;

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
