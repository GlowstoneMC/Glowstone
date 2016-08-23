package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.InboundChatPacket;

import java.io.IOException;

public final class IncomingChatCodec implements Codec<InboundChatPacket> {
    @Override
    public InboundChatPacket decode(ByteBuf buffer) throws IOException {
        return new InboundChatPacket(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buf, InboundChatPacket message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getText());
        return buf;
    }
}
