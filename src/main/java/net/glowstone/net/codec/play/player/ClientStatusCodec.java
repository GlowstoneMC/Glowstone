package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.ClientStatusMessage;

public final class ClientStatusCodec implements Codec<ClientStatusMessage> {

    @Override
    public ClientStatusMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int action = buf.readUnsignedByte();
        return new ClientStatusMessage(action);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ClientStatusMessage message) throws IOException {
        buf.writeByte(message.getAction());
        return buf;
    }
}
