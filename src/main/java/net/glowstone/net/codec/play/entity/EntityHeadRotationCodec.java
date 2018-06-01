package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;

public final class EntityHeadRotationCodec implements Codec<EntityHeadRotationMessage> {

    @Override
    public EntityHeadRotationMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int rotation = buf.readByte();
        return new EntityHeadRotationMessage(id, rotation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityHeadRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getRotation());
        return buf;
    }
}
