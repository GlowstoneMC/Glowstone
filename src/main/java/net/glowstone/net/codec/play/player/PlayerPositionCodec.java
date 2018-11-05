package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerPositionMessage;

public final class PlayerPositionCodec implements Codec<PlayerPositionMessage> {

    @Override
    public PlayerPositionMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        boolean onGround = buffer.readBoolean();

        return new PlayerPositionMessage(onGround, x, y, z);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayerPositionMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
