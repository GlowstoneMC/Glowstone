package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayerListHeaderFooterPacket;
import net.glowstone.util.TextMessage;

import java.io.IOException;

public final class UserListHeaderFooterCodec implements Codec<PlayerListHeaderFooterPacket> {

    @Override
    public PlayerListHeaderFooterPacket decode(ByteBuf buffer) throws IOException {
        TextMessage header = GlowBufUtils.readChat(buffer);
        TextMessage footer = GlowBufUtils.readChat(buffer);
        return new PlayerListHeaderFooterPacket(header, footer);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerListHeaderFooterPacket message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getHeader());
        GlowBufUtils.writeChat(buf, message.getFooter());
        return buf;
    }
}
