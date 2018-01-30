package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.SpectateMessage;

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
