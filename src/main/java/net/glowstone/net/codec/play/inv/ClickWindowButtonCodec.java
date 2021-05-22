package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.inv.ClickWindowButtonMessage;

public final class ClickWindowButtonCodec implements Codec<ClickWindowButtonMessage> {

    @Override
    public ClickWindowButtonMessage decode(ByteBuf buf) throws IOException {
        int window = buf.readByte();
        int button = buf.readByte();
        return new ClickWindowButtonMessage(window, button);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ClickWindowButtonMessage message) throws IOException {
        buf.writeByte(message.getWindow());
        buf.writeByte(message.getButton());
        return buf;
    }
}
