package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.CollectItemMessage;

import java.io.IOException;

public final class CollectItemCodec implements Codec<CollectItemMessage> {
    public CollectItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode CollectItemMessage");
    }

    public ByteBuf encode(ByteBuf buf, CollectItemMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getCollector());
        return buf;
    }
}
