package net.glowstone.net.codec.status;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.status.StatusRequestMessage;

public final class StatusRequestCodec implements Codec<StatusRequestMessage> {

    @Override
    public StatusRequestMessage decode(CodecContext codecContext, ByteBuf byteBuf) throws IOException {
        return new StatusRequestMessage();
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, StatusRequestMessage statusRequestMessage)
        throws IOException {
        return buf;
    }
}
