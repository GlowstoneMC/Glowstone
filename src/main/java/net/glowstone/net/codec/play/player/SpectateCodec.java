package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.SpectateMessage;

import java.io.IOException;
import java.util.UUID;

public final class SpectateCodec implements Codec<SpectateMessage> {
    @Override
    public SpectateMessage decode(ByteBuf buf) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(buf);
        return new SpectateMessage(uuid);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpectateMessage message) throws IOException {
        GlowBufUtils.writeUuid(buf, message.getTarget());
        return buf;
    }
}
