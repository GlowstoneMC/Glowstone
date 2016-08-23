package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayEffectPacket;

import java.io.IOException;

public final class PlayEffectCodec implements Codec<PlayEffectPacket> {
    @Override
    public PlayEffectPacket decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayEffectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayEffectPacket message) throws IOException {
        buf.writeInt(message.getId());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeInt(message.getData());
        buf.writeBoolean(message.isIgnoreDistance());
        return buf;
    }
}
