package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;

public final class SpawnXpOrbCodec implements Codec<SpawnXpOrbMessage> {

    @Override
    public SpawnXpOrbMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        short count = buf.readShort();
        return new SpawnXpOrbMessage(id, x, y, z, count);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnXpOrbMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeShort(message.getCount());
        return buf;
    }
}
