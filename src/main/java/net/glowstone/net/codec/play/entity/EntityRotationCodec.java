package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityRotationMessage;

public final class EntityRotationCodec implements Codec<EntityRotationMessage> {

    @Override
    public EntityRotationMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        boolean ground = buf.readBoolean();
        return new EntityRotationMessage(id, rotation, pitch, ground);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, EntityRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
