package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PluginLoadPacket;

import java.io.IOException;

public final class PluginMessageCodec implements Codec<PluginLoadPacket> {
    @Override
    public PluginLoadPacket decode(ByteBuf buf) throws IOException {
        String channel = ByteBufUtils.readUTF8(buf);

        // todo: maybe store a ByteBuf in the message instead?
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new PluginLoadPacket(channel, data);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PluginLoadPacket message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getChannel());
        buf.writeBytes(message.getData());
        return buf;
    }
}
