package net.glowstone.net.codec;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.KickMessage;
import net.glowstone.util.TextMessage;

public final class KickCodec implements Codec<KickMessage> {

    @Override
    public KickMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        TextMessage value = GlowBufUtils.readChat(buf);
        return new KickMessage(value);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, KickMessage message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        return buf;
    }
}
