package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.SpectatePacket;

import java.io.IOException;
import java.util.UUID;

public final class SpectateCodec implements Codec<SpectatePacket> {
    @Override
    public SpectatePacket decode(ByteBuf buf) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(buf);
        return new SpectatePacket(uuid);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpectatePacket message) throws IOException {
        GlowBufUtils.writeUuid(buf, message.getTarget());
        return buf;
    }
}
