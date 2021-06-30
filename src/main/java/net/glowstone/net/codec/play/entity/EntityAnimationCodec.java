package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityAnimationMessage;

public final class EntityAnimationCodec implements Codec<EntityAnimationMessage> {

    @Override
    public EntityAnimationMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int animation = buf.readUnsignedByte();
        return new EntityAnimationMessage(id, animation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityAnimationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getAnimation());
        return buf;
    }
}
