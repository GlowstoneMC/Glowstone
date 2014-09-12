package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;

import java.io.IOException;

public final class SpawnXpOrbCodec implements Codec<SpawnXpOrbMessage> {
    @Override
    public SpawnXpOrbMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        short count = buf.readShort();
        return new SpawnXpOrbMessage(id, x, y, z, count);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnXpOrbMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeShort(message.getCount());
        return buf;
    }
}
