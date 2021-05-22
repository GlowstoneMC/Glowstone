package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import net.glowstone.entity.meta.MetadataType;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayParticleMessage;

public final class PlayParticleCodec implements Codec<PlayParticleMessage> {

    @Override
    public PlayParticleMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayParticleMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayParticleMessage message) throws IOException {
        buf.writeInt(message.getParticle());
        buf.writeBoolean(message.isLongDistance());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
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
