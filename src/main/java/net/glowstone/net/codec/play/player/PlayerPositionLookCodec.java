package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionLookPacket;

import java.io.IOException;

public final class PlayerPositionLookCodec implements Codec<PlayerPositionLookPacket> {
    @Override
    public PlayerPositionLookPacket decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionLookPacket(onGround, x, y, z, yaw, pitch);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerPositionLookPacket message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
