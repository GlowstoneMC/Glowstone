package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerPositionMessage;

import java.io.IOException;

public final class PlayerPositionCodec implements Codec<PlayerPositionMessage> {

    @Override
    public PlayerPositionMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double stance = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        boolean onGround = buffer.readByte() != 0;

        return new PlayerPositionMessage(onGround, x, stance, y, z);
    }

    @Override
    public void encode(ByteBuf buf, PlayerPositionMessage message) throws IOException {
        buf.writeDouble(buf.readDouble());
        buf.writeDouble(message.getStance());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getX());
        buf.writeByte(message.getOnGround() ? 1 : 0);

    }
}
