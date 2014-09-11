package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.CollectItemMessage;

import java.io.IOException;

public final class CollectItemCodec implements Codec<CollectItemMessage> {
    @Override
    public CollectItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode CollectItemMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CollectItemMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getCollector());
        return buf;
    }
}
