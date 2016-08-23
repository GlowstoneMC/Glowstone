package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityHeadRotationPacket;

import java.io.IOException;

public final class EntityHeadRotationCodec implements Codec<EntityHeadRotationPacket> {
    @Override
    public EntityHeadRotationPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int rotation = buf.readByte();
        return new EntityHeadRotationPacket(id, rotation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityHeadRotationPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getRotation());
        return buf;
    }
}
