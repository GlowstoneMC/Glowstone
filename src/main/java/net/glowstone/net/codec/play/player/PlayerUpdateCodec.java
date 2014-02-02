package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;

import java.io.IOException;

public final class PlayerUpdateCodec implements Codec<PlayerUpdateMessage> {
    public PlayerUpdateMessage decode(ByteBuf buffer) throws IOException {
        return new PlayerUpdateMessage(buffer.readByte() != 0);
    }

    public ByteBuf encode(ByteBuf buf, PlayerUpdateMessage message) throws IOException {
        buf.writeByte(message.getOnGround() ? 1 : 0);
        return buf;
    }
}
