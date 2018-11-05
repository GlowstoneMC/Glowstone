package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.PingMessage;

public final class PingCodec implements Codec<PingMessage> {

    @Override
    public PingMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        return new PingMessage(buffer.readLong());
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PingMessage message) throws IOException {
        buf.writeLong(message.getPingId());
        return buf;
    }
}
