package net.glowstone.net.codec.status;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.status.StatusPingMessage;

public final class StatusPingCodec implements Codec<StatusPingMessage> {

    @Override
    public StatusPingMessage decode(ByteBuf byteBuf) throws IOException {
        return new StatusPingMessage(byteBuf.readLong());
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, StatusPingMessage statusPingMessage) throws IOException {
        byteBuf.writeLong(statusPingMessage.getTime());
        return byteBuf;
    }
}
