package net.glowstone.net.codec.status;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.status.StatusResponseMessage;

import java.io.IOException;

public final class StatusResponseCodec implements Codec<StatusResponseMessage> {
    @Override
    public StatusResponseMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode StatusResponseMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StatusResponseMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getJson());
        return buf;
    }
}
