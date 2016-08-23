package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.OutboundChatPacket;
import net.glowstone.util.TextMessage;

import java.io.IOException;

public final class ChatCodec implements Codec<OutboundChatPacket> {
    @Override
    public OutboundChatPacket decode(ByteBuf buf) throws IOException {
        TextMessage message = GlowBufUtils.readChat(buf);
        int mode = buf.readByte();
        return new OutboundChatPacket(message, mode);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, OutboundChatPacket message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        buf.writeByte(message.getMode());
        return buf;
    }
}
