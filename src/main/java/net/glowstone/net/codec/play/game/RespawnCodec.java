package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.RespawnMessage;

import java.io.IOException;

public final class RespawnCodec implements Codec<RespawnMessage> {
    @Override
    public RespawnMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode RespawnMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RespawnMessage message) throws IOException {
        buf.writeInt(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMode());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        return buf;
    }
}
