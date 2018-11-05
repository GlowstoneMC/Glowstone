package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerPositionLookMessage;

public final class PlayerPositionLookCodec implements Codec<PlayerPositionLookMessage> {

    @Override
    public PlayerPositionLookMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionLookMessage(onGround, x, y, z, yaw, pitch);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayerPositionLookMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
