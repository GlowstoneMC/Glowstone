package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerActionPacket;

import java.io.IOException;

public final class PlayerActionCodec implements Codec<PlayerActionPacket> {
    @Override
    public PlayerActionPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int action = buf.readByte();
        int jumpBoost = ByteBufUtils.readVarInt(buf);
        return new PlayerActionPacket(id, action, jumpBoost);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerActionPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getAction());
        ByteBufUtils.writeVarInt(buf, message.getJumpBoost());
        return buf;
    }
}
