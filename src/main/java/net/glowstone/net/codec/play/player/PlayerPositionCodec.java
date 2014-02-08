package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionMessage;

import java.io.IOException;

public final class PlayerPositionCodec implements Codec<PlayerPositionMessage> {
    public PlayerPositionMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double headY = buffer.readDouble();
        double z = buffer.readDouble();
        boolean onGround = buffer.readByte() != 0;

        return new PlayerPositionMessage(onGround, x, y, headY, z);
    }

    public ByteBuf encode(ByteBuf buf, PlayerPositionMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getHeadY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getOnGround() ? 1 : 0);
        return buf;
    }
}
