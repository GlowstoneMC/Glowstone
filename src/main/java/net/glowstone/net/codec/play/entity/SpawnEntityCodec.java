package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnEntityMessage;

import java.io.IOException;
import java.util.UUID;

public final class SpawnEntityCodec implements Codec<SpawnEntityMessage> {

    @Override
    public SpawnEntityMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        UUID uuid = GlowBufUtils.readUuid(buf);
        int type = ByteBufUtils.readVarInt(buf);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int pitch = buf.readByte();
        int yaw = buf.readByte();
        int headYaw = buf.readByte();
        int data = ByteBufUtils.readVarInt(buf);
        int velX = buf.readShort();
        int velY = buf.readShort();
        int velZ = buf.readShort();
        return new SpawnEntityMessage(id, uuid, type, x, y, z, pitch, yaw, headYaw, data, velX, velY, velZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
        ByteBufUtils.writeVarInt(buf, message.getType());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getYaw());
        buf.writeByte(message.getHeadYaw());
        ByteBufUtils.writeVarInt(buf, message.getData());
        buf.writeShort(message.getVelX());
        buf.writeShort(message.getVelY());
        buf.writeShort(message.getVelZ());
        return buf;
    }
}
