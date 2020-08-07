package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnMobMessage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class SpawnMobCodec implements Codec<SpawnMobMessage> {

    @Override
    public SpawnMobMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        UUID uuid = GlowBufUtils.readUuid(buf);
        int type = ByteBufUtils.readVarInt(buf);
        ;
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int headPitch = buf.readByte();
        int pitch = buf.readByte();
        int rotation = buf.readByte();
        int velX = buf.readShort();
        int velY = buf.readShort();
        int velZ = buf.readShort();
        List<Entry> list = GlowBufUtils.readMetadata(buf);
        return new SpawnMobMessage(id, uuid, type, x, y, z, rotation, pitch, headPitch, velX, velY,
            velZ, list);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnMobMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
        ByteBufUtils.writeVarInt(buf, message.getType());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getHeadPitch());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getRotation());
        buf.writeShort(message.getVelX());
        buf.writeShort(message.getVelY());
        buf.writeShort(message.getVelZ());
        GlowBufUtils.writeMetadata(buf, message.getMetadata());
        return buf;
    }
}
