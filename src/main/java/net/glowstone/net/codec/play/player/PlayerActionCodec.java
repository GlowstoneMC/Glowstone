package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerActionMessage;

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
