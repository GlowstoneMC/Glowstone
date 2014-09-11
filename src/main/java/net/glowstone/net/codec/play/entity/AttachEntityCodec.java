package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.AttachEntityMessage;

import java.io.IOException;

public final class AttachEntityCodec implements Codec<AttachEntityMessage> {
    @Override
    public AttachEntityMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode AttachEntityMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AttachEntityMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getVehicle());
        buf.writeBoolean(message.isLeash());
        return buf;
    }
}
