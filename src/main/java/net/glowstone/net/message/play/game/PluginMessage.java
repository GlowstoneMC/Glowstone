package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import net.glowstone.GlowServer;

import java.io.IOException;
import java.util.logging.Level;

@Data
public final class PluginMessage implements Message {

    private final String channel;
    private final byte[] data;

    public static PluginMessage fromString(String channel, String text) {
        ByteBuf buf = Unpooled.buffer(5 + text.length());
        try {
            ByteBufUtils.writeUTF8(buf, text);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Error converting to PluginMessage: \"" + channel + "\", \"" + text + "\"", e);
        }
        return new PluginMessage(channel, buf.array());
    }

}

