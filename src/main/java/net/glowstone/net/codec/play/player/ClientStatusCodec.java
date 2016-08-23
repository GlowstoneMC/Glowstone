package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ClientStatusPacket;

import java.io.IOException;

public final class ClientStatusCodec implements Codec<ClientStatusPacket> {
    @Override
    public ClientStatusPacket decode(ByteBuf buf) throws IOException {
        int action = buf.readUnsignedByte();
        return new ClientStatusPacket(action);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ClientStatusPacket message) throws IOException {
        buf.writeByte(message.getAction());
        return buf;
    }
}
