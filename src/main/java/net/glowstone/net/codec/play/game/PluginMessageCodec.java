package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.NamespacedKey;

import java.io.IOException;

public final class PluginMessageCodec implements Codec<PluginMessage> {

    @Override
    public PluginMessage decode(ByteBuf buf) throws IOException {
        NamespacedKey channel = GlowBufUtils.readNamespacedKey(buf);

        // todo: maybe store a ByteBuf in the message instead?
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new PluginMessage(channel.toString(), data);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PluginMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getChannel());
        buf.writeBytes(message.getData());
        return buf;
    }
}
