package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;

import java.io.IOException;

public final class EntityTeleportCodec implements Codec<EntityTeleportMessage> {
    @Override
    public EntityTeleportMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        boolean ground = buf.readBoolean();
        return new EntityTeleportMessage(id, x, y, z, rotation, pitch, ground);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityTeleportMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
