package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnMobMessage;

import java.io.IOException;
import java.util.List;

public final class SpawnMobCodec implements Codec<SpawnMobMessage> {
    @Override
    public SpawnMobMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int type = buf.readByte();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int headPitch = buf.readByte();
        int pitch = buf.readByte();
        int rotation = buf.readByte();
        int velX = buf.readShort();
        int velY = buf.readShort();
        int velZ = buf.readShort();
        List<MetadataMap.Entry> list = GlowBufUtils.readMetadata(buf);
        return new SpawnMobMessage(id, type, x, y, z, rotation, pitch, headPitch, velX, velY, velZ, list);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnMobMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getType());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
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
