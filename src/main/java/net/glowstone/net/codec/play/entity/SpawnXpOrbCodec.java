package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SpawnXpOrbPacket;

import java.io.IOException;

public final class SpawnXpOrbCodec implements Codec<SpawnXpOrbPacket> {
    @Override
    public SpawnXpOrbPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        short count = buf.readShort();
        return new SpawnXpOrbPacket(id, x, y, z, count);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnXpOrbPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeShort(message.getCount());
        return buf;
    }
}
