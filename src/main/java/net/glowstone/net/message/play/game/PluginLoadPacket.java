package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import net.glowstone.GlowServer;

import java.io.IOException;
import java.util.logging.Level;

@Data
public final class PluginLoadPacket implements Message {

    private final String channel;
    private final byte[] data;

    public static PluginLoadPacket fromString(String channel, String text) {
        ByteBuf buf = Unpooled.buffer(5 + text.length());
        try {
            ByteBufUtils.writeUTF8(buf, text);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Error converting to PluginMessage: \"" + channel + "\", \"" + text + "\"", e);
        }
        byte[] array = buf.array();
        buf.release();
        return new PluginLoadPacket(channel, array);
    }

}

