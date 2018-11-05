package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.AttachEntityMessage;

public final class AttachEntityCodec implements Codec<AttachEntityMessage> {

    @Override
    public AttachEntityMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int attached = buf.readInt();
        int holding = buf.readInt();
        return new AttachEntityMessage(attached, holding);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, AttachEntityMessage message) throws IOException {
        buf.writeInt(message.getAttached());
        buf.writeInt(message.getHolding());
        return buf;
    }
}
