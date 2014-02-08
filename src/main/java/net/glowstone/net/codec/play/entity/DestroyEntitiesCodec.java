package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;

import java.io.IOException;

public final class DestroyEntitiesCodec implements Codec<DestroyEntitiesMessage> {
    public DestroyEntitiesMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode DestroyEntitiesMessage");
    }

    public ByteBuf encode(ByteBuf buf, DestroyEntitiesMessage message) throws IOException {
        buf.writeByte(message.getIds().size());
        for (int id : message.getIds()) {
            buf.writeInt(id);
        }
        return buf;
    }
}
