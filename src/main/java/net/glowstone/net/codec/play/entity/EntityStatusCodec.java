package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityStatusMessage;

import java.io.IOException;

public final class EntityStatusCodec implements Codec<EntityStatusMessage> {

    @Override
    public EntityStatusMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int status = buf.readByte();
        return new EntityStatusMessage(id, status);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityStatusMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getStatus());
        return buf;
    }
}
