package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PingMessage;

import java.io.IOException;

public final class PingCodec implements Codec<PingMessage> {
    @Override
    public PingMessage decode(ByteBuf buffer) throws IOException {
        return new PingMessage(ByteBufUtils.readVarInt(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PingMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getPingId());
        return buf;
    }
}
