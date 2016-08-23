package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PingPacket;

import java.io.IOException;

public final class PingCodec implements Codec<PingPacket> {
    @Override
    public PingPacket decode(ByteBuf buffer) throws IOException {
        return new PingPacket(ByteBufUtils.readVarInt(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PingPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getPingId());
        return buf;
    }
}
