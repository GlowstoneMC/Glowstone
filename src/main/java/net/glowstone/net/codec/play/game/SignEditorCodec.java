package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.SignEditorMessage;

import java.io.IOException;

public final class SignEditorCodec implements Codec<SignEditorMessage> {
    @Override
    public SignEditorMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SignEditorMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SignEditorMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        return buf;
    }
}
