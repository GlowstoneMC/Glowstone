package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ChatMessage;

import java.io.IOException;

public final class ChatCodec implements Codec<ChatMessage> {
    @Override
    public ChatMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode (outgoing) ChatMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChatMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getJson());
        buf.writeByte(message.getMode());
        return buf;
    }
}
