package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionMessage;

import java.io.IOException;

public final class PlayerPositionCodec implements Codec<PlayerPositionMessage> {
    @Override
    public PlayerPositionMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionMessage(onGround, x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerPositionMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeBoolean(message.getOnGround());
        return buf;
    }
}
