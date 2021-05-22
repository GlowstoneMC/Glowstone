package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;

public final class PlayerUpdateCodec implements Codec<PlayerUpdateMessage> {

    @Override
    public PlayerUpdateMessage decode(ByteBuf buffer) throws IOException {
        boolean onGround = buffer.readBoolean();
        return new PlayerUpdateMessage(onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerUpdateMessage message) throws IOException {
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
