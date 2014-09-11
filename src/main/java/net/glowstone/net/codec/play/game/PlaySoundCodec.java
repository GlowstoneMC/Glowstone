package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.PlaySoundMessage;

import java.io.IOException;

public final class PlaySoundCodec implements Codec<PlaySoundMessage> {
    @Override
    public PlaySoundMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlaySoundMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlaySoundMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getSound());
        buf.writeInt((int) (8 * message.getX()));
        buf.writeInt((int) (8 * message.getY()));
        buf.writeInt((int) (8 * message.getZ()));
        buf.writeFloat(message.getVolume());
        buf.writeByte((int) (message.getPitch() * 63));
        return buf;
    }
}
