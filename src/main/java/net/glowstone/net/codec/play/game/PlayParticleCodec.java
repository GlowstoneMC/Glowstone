package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.meta.MetadataType;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayParticleMessage;

import java.io.IOException;

public final class PlayParticleCodec implements Codec<PlayParticleMessage> {

    @Override
    public PlayParticleMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayParticleMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayParticleMessage message) throws IOException {
        buf.writeInt(message.getParticle());
        buf.writeBoolean(message.isLongDistance());
        buf.writeFloat(message.getX());
        buf.writeFloat(message.getY());
        buf.writeFloat(message.getZ());
        buf.writeFloat(message.getOfsX());
        buf.writeFloat(message.getOfsY());
        buf.writeFloat(message.getOfsZ());
        buf.writeFloat(message.getData());
        buf.writeInt(message.getCount());

        for (Object extData : message.getExtData()) {
            MetadataType type = MetadataType.byClass(extData.getClass());
            if (type != null) {
                GlowBufUtils.writeValue(buf, extData, type);
            }
        }

        return buf;
    }
}
