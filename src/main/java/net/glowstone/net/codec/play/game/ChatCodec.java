package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChatMessage;
import net.glowstone.util.TextMessage;

import java.io.IOException;

public final class ChatCodec implements Codec<ChatMessage> {
    @Override
    public ChatMessage decode(ByteBuf buf) throws IOException {
        String message = ByteBufUtils.readUTF8(buf);
        int mode = buf.readByte();
        return new ChatMessage(TextMessage.decode(message), mode);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChatMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        buf.writeByte(message.getMode());
        return buf;
    }
}
