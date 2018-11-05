package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.IncomingChatMessage;

public final class IncomingChatCodec implements Codec<IncomingChatMessage> {

    @Override
    public IncomingChatMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        return new IncomingChatMessage(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, IncomingChatMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getText());
        return buf;
    }
}
