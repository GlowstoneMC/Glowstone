package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayEffectMessage;

public final class PlayEffectCodec implements Codec<PlayEffectMessage> {

    @Override
    public PlayEffectMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayEffectMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayEffectMessage message) throws IOException {
        buf.writeInt(message.getId());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeInt(message.getData());
        buf.writeBoolean(message.isIgnoreDistance());
        return buf;
    }
}
