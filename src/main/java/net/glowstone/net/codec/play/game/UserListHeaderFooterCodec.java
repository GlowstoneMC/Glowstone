package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UserListHeaderFooterMessage;
import net.glowstone.util.TextMessage;

public final class UserListHeaderFooterCodec implements Codec<UserListHeaderFooterMessage> {

    @Override
    public UserListHeaderFooterMessage decode(ByteBuf buffer) throws IOException {
        TextMessage header = GlowBufUtils.readChat(buffer);
        TextMessage footer = GlowBufUtils.readChat(buffer);
        return new UserListHeaderFooterMessage(header, footer);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UserListHeaderFooterMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getHeader());
        GlowBufUtils.writeChat(buf, message.getFooter());
        return buf;
    }
}
