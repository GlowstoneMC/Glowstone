package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.CollectItemPacket;

import java.io.IOException;

public final class CollectItemCodec implements Codec<CollectItemPacket> {
    @Override
    public CollectItemPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int collector = ByteBufUtils.readVarInt(buf);
        return new CollectItemPacket(id, collector);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CollectItemPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getCollector());
        return buf;
    }
}
