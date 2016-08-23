package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerUpdatePacket;

import java.io.IOException;

public final class PlayerUpdateCodec implements Codec<PlayerUpdatePacket> {
    @Override
    public PlayerUpdatePacket decode(ByteBuf buffer) throws IOException {
        boolean onGround = buffer.readBoolean();
        return new PlayerUpdatePacket(onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerUpdatePacket message) throws IOException {
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
