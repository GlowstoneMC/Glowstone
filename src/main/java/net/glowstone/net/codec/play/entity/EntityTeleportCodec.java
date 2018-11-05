package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;

public final class EntityTeleportCodec implements Codec<EntityTeleportMessage> {

    @Override
    public EntityTeleportMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        boolean ground = buf.readBoolean();
        return new EntityTeleportMessage(id, x, y, z, rotation, pitch, ground);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, EntityTeleportMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
