package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerLookPacket;

import java.io.IOException;

public final class PlayerLookCodec implements Codec<PlayerLookPacket> {
    @Override
    public PlayerLookPacket decode(ByteBuf buffer) throws IOException {
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readBoolean();
        return new PlayerLookPacket(yaw, pitch, onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerLookPacket message) throws IOException {
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
