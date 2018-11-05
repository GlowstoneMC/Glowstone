package net.glowstone.net.codec.handshake;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.handshake.HandshakeMessage;

public final class HandshakeCodec implements Codec<HandshakeMessage> {

    @Override
    public HandshakeMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        int version = ByteBufUtils.readVarInt(buffer);
        String address = ByteBufUtils.readUTF8(buffer);
        int port = buffer.readUnsignedShort();
        int state = ByteBufUtils.readVarInt(buffer);

        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, HandshakeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getVersion());
        ByteBufUtils.writeUTF8(buf, message.getAddress());
        buf.writeShort(message.getPort());
        ByteBufUtils.writeVarInt(buf, message.getState());
        return buf;
    }
}
