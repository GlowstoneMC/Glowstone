package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;

import java.io.IOException;

public final class SpawnPlayerCodec implements Codec<SpawnPlayerMessage> {
    public SpawnPlayerMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPlayerMessage");
    }

    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        buf.writeInt(message.getId());
        //ByteBufUtils.writeUTF8(message.get);
        // todo
        return buf;
    }
}
