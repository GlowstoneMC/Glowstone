package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.UserListItemMessage;

import java.io.IOException;

public final class UserListItemCodec implements Codec<UserListItemMessage> {
    public UserListItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode UserListItemMessage");
    }

    public ByteBuf encode(ByteBuf buf, UserListItemMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getName());
        buf.writeBoolean(message.getOnline());
        buf.writeShort(message.getPing());
        return buf;
    }
}
