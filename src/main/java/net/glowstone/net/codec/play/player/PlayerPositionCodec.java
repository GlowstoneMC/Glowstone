package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionPacket;

import java.io.IOException;

public final class PlayerPositionCodec implements Codec<PlayerPositionPacket> {
    @Override
    public PlayerPositionPacket decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionPacket(onGround, x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerPositionPacket message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
