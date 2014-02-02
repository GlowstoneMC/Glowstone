package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PingMessage;

import java.io.IOException;

public final class PingCodec implements Codec<PingMessage> {
    public PingMessage decode(ByteBuf buffer) throws IOException {
        return new PingMessage(buffer.readInt());
    }

    public ByteBuf encode(ByteBuf buf, PingMessage message) throws IOException {
        buf.writeInt(message.getPingId());
        return buf;
    }
}
