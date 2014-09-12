package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;

import java.io.IOException;

public final class SpawnObjectCodec implements Codec<SpawnObjectMessage> {
    @Override
    public SpawnObjectMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int type = buf.readByte();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int pitch = buf.readByte();
        int yaw = buf.readByte();
        int data = buf.readInt();
        if (data != 0) {
            int velX = buf.readShort();
            int velY = buf.readShort();
            int velZ = buf.readShort();
            return new SpawnObjectMessage(id, type, x, y, z, pitch, yaw, data, velX, velY, velZ);
        }
        return new SpawnObjectMessage(id, type, x, y, z, pitch, yaw);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnObjectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getType());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getYaw());
        buf.writeInt(message.getData());
        if (message.getData() != 0) {
            buf.writeShort(message.getVelX());
            buf.writeShort(message.getVelY());
            buf.writeShort(message.getVelZ());
        }
        return buf;
    }
}
