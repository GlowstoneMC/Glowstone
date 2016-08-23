package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.TeleportConfirmPacket;

import java.io.IOException;

public class TeleportConfirmCodec implements Codec<TeleportConfirmPacket> {
    @Override
    public TeleportConfirmPacket decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        return new TeleportConfirmPacket(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TeleportConfirmPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTeleportID());
        return buf;
    }
}
