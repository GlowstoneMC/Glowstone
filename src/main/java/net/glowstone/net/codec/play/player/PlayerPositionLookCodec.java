package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionLookMessage;

import java.io.IOException;

public final class PlayerPositionLookCodec implements Codec<PlayerPositionLookMessage> {
    @Override
    public PlayerPositionLookMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionLookMessage(onGround, x, y, z, yaw, pitch);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerPositionLookMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        buf.writeBoolean(message.getOnGround());
        return buf;
    }
}
