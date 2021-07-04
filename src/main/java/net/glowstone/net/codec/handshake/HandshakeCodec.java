package net.glowstone.net.codec.handshake;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.handshake.HandshakeMessage;

import java.io.IOException;

public final class HandshakeCodec implements Codec<HandshakeMessage> {

    @Override
    public HandshakeMessage decode(ByteBuf buffer) throws IOException {
        int version = ByteBufUtils.readVarInt(buffer);
        String address = ByteBufUtils.readUTF8(buffer);
        int port = buffer.readUnsignedShort();
        int state = ByteBufUtils.readVarInt(buffer);

        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getVersion());
        ByteBufUtils.writeUTF8(buf, message.getAddress());
        buf.writeShort(message.getPort());
        ByteBufUtils.writeVarInt(buf, message.getState());
        return buf;
    }
}
