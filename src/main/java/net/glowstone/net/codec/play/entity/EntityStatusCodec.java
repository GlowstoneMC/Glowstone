package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityStatusPacket;

import java.io.IOException;

public final class EntityStatusCodec implements Codec<EntityStatusPacket> {
    @Override
    public EntityStatusPacket decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int status = buf.readByte();
        return new EntityStatusPacket(id, status);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityStatusPacket message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getStatus());
        return buf;
    }
}
