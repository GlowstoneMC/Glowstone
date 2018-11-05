package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChatMessage;
import net.glowstone.util.TextMessage;

public final class ChatCodec implements Codec<ChatMessage> {

    @Override
    public ChatMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        TextMessage message = GlowBufUtils.readChat(buf);
        int mode = buf.readByte();
        return new ChatMessage(message, mode);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ChatMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        buf.writeByte(message.getMode());
        return buf;
    }
}
