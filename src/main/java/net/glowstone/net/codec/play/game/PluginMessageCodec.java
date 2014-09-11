package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PluginMessage;

import java.io.IOException;

public final class PluginMessageCodec implements Codec<PluginMessage> {
    @Override
    public PluginMessage decode(ByteBuf buf) throws IOException {
        String channel = ByteBufUtils.readUTF8(buf);

        // todo: maybe store a ByteBuf in the message instead?
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new PluginMessage(channel, data);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PluginMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getChannel());
        buf.writeBytes(message.getData());
        return buf;
    }
}
