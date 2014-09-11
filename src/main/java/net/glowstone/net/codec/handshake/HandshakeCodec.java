package net.glowstone.net.codec.handshake;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.handshake.HandshakeMessage;

import java.io.IOException;

public final class HandshakeCodec implements Codec<HandshakeMessage> {
    @Override
    public HandshakeMessage decode(ByteBuf buffer) throws IOException {
        final int version = ByteBufUtils.readVarInt(buffer);
        final String address = ByteBufUtils.readUTF8(buffer);
        final int port = buffer.readUnsignedShort();
        final int state = ByteBufUtils.readVarInt(buffer);

        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getVersion());
        ByteBufUtils.writeUTF8(buf, message.getAddress());
        buf.writeShort(message.getPort());
        buf.writeInt(message.getPort());
        return buf;
    }
}
