package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerLookMessage;

public final class PlayerLookCodec implements Codec<PlayerLookMessage> {

    @Override
    public PlayerLookMessage decode(ByteBuf buffer) throws IOException {
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readBoolean();
        return new PlayerLookMessage(yaw, pitch, onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerLookMessage message) throws IOException {
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
