package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.PluginMessage;

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
