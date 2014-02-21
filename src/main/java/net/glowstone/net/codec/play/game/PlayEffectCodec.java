package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.PlayEffectMessage;

import java.io.IOException;

public final class PlayEffectCodec implements Codec<PlayEffectMessage> {
    public PlayEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode PlayEffectMessage");
    }

    public ByteBuf encode(ByteBuf buf, PlayEffectMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getX());
        buf.writeByte(message.getY());
        buf.writeInt(message.getZ());
        buf.writeInt(message.getData());
        buf.writeBoolean(message.getIgnoreDistance());
        return buf;
    }
}
