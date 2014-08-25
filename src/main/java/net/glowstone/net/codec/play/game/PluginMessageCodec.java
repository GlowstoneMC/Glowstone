package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PluginMessage;

import java.io.IOException;

public final class PluginMessageCodec implements Codec<PluginMessage> {
    public PluginMessage decode(ByteBuf buf) throws IOException {
        String channel = ByteBufUtils.readUTF8(buf);

        // todo: ReplayingDecoderBuffer basically makes this impossible to handle
        // need to probably switch to proper frame-based decoding.
        int length = buf.writerIndex() - buf.readerIndex();
        byte[] data = new byte[length];
        buf.readBytes(data);
        return new PluginMessage(channel, data);
    }

    public ByteBuf encode(ByteBuf buf, PluginMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getChannel());
        buf.writeBytes(message.getData());
        return buf;
    }
}
