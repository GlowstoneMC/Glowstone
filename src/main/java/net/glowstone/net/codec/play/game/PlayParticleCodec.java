package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.PlayParticleMessage;

import java.io.IOException;

public final class PlayParticleCodec implements Codec<PlayParticleMessage> {
    public PlayParticleMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayParticleMessage");
    }

    public ByteBuf encode(ByteBuf buf, PlayParticleMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getParticle());
        buf.writeFloat(message.getX());
        buf.writeFloat(message.getY());
        buf.writeFloat(message.getZ());
        buf.writeFloat(message.getOfsX());
        buf.writeFloat(message.getOfsY());
        buf.writeFloat(message.getOfsZ());
        buf.writeFloat(message.getData());
        buf.writeInt(message.getCount());
        return buf;
    }
}
