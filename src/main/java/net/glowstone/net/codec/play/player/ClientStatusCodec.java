package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ClientStatusMessage;

import java.io.IOException;

public final class ClientStatusCodec implements Codec<ClientStatusMessage> {
    public ClientStatusMessage decode(ByteBuf buf) throws IOException {
        int action = buf.readByte();
        return new ClientStatusMessage(action);
    }

    public ByteBuf encode(ByteBuf buf, ClientStatusMessage message) throws IOException {
        buf.writeByte(message.getAction());
        return buf;
    }
}
