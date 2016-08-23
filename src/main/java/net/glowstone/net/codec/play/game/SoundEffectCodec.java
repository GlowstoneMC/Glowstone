package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.SoundEffectPacket;

import java.io.IOException;

public class SoundEffectCodec implements Codec<SoundEffectPacket> {
    @Override
    public SoundEffectPacket decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode SoundEffectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SoundEffectPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getSound());
        ByteBufUtils.writeVarInt(buf, message.getCategory().ordinal());
        buf.writeInt((int) (8 * message.getX()));
        buf.writeInt((int) (8 * message.getY()));
        buf.writeInt((int) (8 * message.getZ()));
        buf.writeFloat(message.getVolume());
        buf.writeFloat(message.getPitch());
        return buf;
    }
}
