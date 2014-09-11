package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;

import java.io.IOException;

public final class DestroyEntitiesCodec implements Codec<DestroyEntitiesMessage> {
    @Override
    public DestroyEntitiesMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode DestroyEntitiesMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DestroyEntitiesMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getIds().size());
        for (int id : message.getIds()) {
            ByteBufUtils.writeVarInt(buf, id);
        }
        return buf;
    }
}
