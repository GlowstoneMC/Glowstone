package net.glowstone.net.codec.status;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.status.StatusRequestMessage;

import java.io.IOException;

public final class StatusRequestCodec implements Codec<StatusRequestMessage>{
    public StatusRequestMessage decode(ByteBuf byteBuf) throws IOException {
        return new StatusRequestMessage();
    }

    public ByteBuf encode(ByteBuf buf, StatusRequestMessage statusRequestMessage) throws IOException {
        return buf;
    }
}
