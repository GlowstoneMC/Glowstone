package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayEffectMessage;

import java.io.IOException;

public final class PlayEffectCodec implements Codec<PlayEffectMessage> {
    @Override
    public PlayEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayEffectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayEffectMessage message) throws IOException {
        buf.writeInt(message.getId());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeInt(message.getData());
        buf.writeBoolean(message.getIgnoreDistance());
        return buf;
    }
}
