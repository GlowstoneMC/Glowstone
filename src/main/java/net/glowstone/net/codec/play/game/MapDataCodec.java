package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.MapDataMessage;

import java.io.IOException;

public final class MapDataCodec implements Codec<MapDataMessage> {
    public MapDataMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MapDataMessage");
    }

    public ByteBuf encode(ByteBuf buf, MapDataMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getData().length);
        buf.writeBytes(message.getData());
        return buf;
    }
}
