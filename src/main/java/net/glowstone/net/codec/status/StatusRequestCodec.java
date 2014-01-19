package net.glowstone.net.codec.status;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.status.StatusRequestMessage;

import java.io.IOException;

public final class StatusRequestCodec implements Codec<StatusRequestMessage>{

    @Override
    public StatusRequestMessage decode(ByteBuf byteBuf) throws IOException {
        System.out.println("Decoding status request");
        return new StatusRequestMessage();
    }

    @Override
    public void encode(ByteBuf byteBuf, StatusRequestMessage statusRequestMessage) throws IOException {
    }
}
