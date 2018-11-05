package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.CollectItemMessage;

public final class CollectItemCodec implements Codec<CollectItemMessage> {

    @Override
    public CollectItemMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int collector = ByteBufUtils.readVarInt(buf);
        int count = ByteBufUtils.readVarInt(buf);
        return new CollectItemMessage(id, collector, count);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, CollectItemMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getCollector());
        ByteBufUtils.writeVarInt(buf, message.getCount());
        return buf;
    }
}
