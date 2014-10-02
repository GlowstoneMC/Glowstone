package net.glowstone.net.codec;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.KickMessage;

import java.io.IOException;

public final class KickCodec implements Codec<KickMessage> {
    @Override
    public KickMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode KickMessage.");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, KickMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        return buf;
    }
}
