package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;

import java.io.IOException;
import java.util.UUID;

public final class SpawnObjectCodec implements Codec<SpawnObjectMessage> {

    @Override
    public SpawnObjectMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        UUID uuid = GlowBufUtils.readUuid(buf);
        int type = buf.readByte();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int pitch = buf.readByte();
        int yaw = buf.readByte();
        int headYaw = buf.readByte();
        int data = buf.readInt();
        int velX = buf.readShort();
        int velY = buf.readShort();
        int velZ = buf.readShort();
        return new SpawnObjectMessage(id, uuid, type, x, y, z, pitch, yaw, headYaw, data, velX, velY, velZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnObjectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
        buf.writeByte(message.getType());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getYaw());
        buf.writeByte(message.getHeadYaw());
        buf.writeInt(message.getData());
        buf.writeShort(message.getVelX());
        buf.writeShort(message.getVelY());
        buf.writeShort(message.getVelZ());
        return buf;
    }
}
