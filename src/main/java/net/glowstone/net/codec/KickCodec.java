package net.glowstone.net.codec;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.KickPacket;
import net.glowstone.util.TextMessage;

import java.io.IOException;

public final class KickCodec implements Codec<KickPacket> {
    @Override
    public KickPacket decode(ByteBuf buf) throws IOException {
        TextMessage value = GlowBufUtils.readChat(buf);
        return new KickPacket(value);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, KickPacket message) throws IOException {
        GlowBufUtils.writeChat(buf, message.getText());
        return buf;
    }
}
