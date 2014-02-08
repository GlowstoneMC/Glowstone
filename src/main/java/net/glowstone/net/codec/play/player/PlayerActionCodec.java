package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerActionMessage;

import java.io.IOException;

public final class PlayerActionCodec implements Codec<PlayerActionMessage> {
    public PlayerActionMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int action = buf.readByte();
        int jumpBoost = buf.readInt();
        return new PlayerActionMessage(id, action, jumpBoost);
    }

    public ByteBuf encode(ByteBuf buf, PlayerActionMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getAction());
        buf.writeInt(message.getJumpBoost());
        return buf;
    }
}
