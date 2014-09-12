package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.CollectItemMessage;

import java.io.IOException;

public final class CollectItemCodec implements Codec<CollectItemMessage> {
    @Override
    public CollectItemMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int collector = ByteBufUtils.readVarInt(buf);
        return new CollectItemMessage(id, collector);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CollectItemMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getCollector());
        return buf;
    }
}
