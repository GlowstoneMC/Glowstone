package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerActionMessage;

import java.io.IOException;

public final class PlayerActionCodec implements Codec<PlayerActionMessage> {
    @Override
    public PlayerActionMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int action = buf.readByte();
        int jumpBoost = ByteBufUtils.readVarInt(buf);
        return new PlayerActionMessage(id, action, jumpBoost);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerActionMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getAction());
        ByteBufUtils.writeVarInt(buf, message.getJumpBoost());
        return buf;
    }
}
