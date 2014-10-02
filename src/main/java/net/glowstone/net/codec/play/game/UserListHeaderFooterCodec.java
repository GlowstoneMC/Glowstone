package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UserListHeaderFooterMessage;

import java.io.IOException;

public final class UserListHeaderFooterCodec implements Codec<UserListHeaderFooterMessage> {

    @Override
    public UserListHeaderFooterMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode PlayerListHeaderFooterMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UserListHeaderFooterMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getHeader());
        GlowBufUtils.writeChat(buf, message.getFooter());
        return buf;
    }
}
