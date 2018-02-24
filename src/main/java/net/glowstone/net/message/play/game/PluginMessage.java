package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.logging.Level;
import lombok.Data;
import net.glowstone.GlowServer;

@Data
public final class PluginMessage implements Message {

    private final String channel;
    private final byte[] data;

    /**
     * Creates a message whose contents are a string in UTF8.
     *
     * @param channel the plugin message channel
     * @param text the contents as a string
     * @return a message for {@code channel} containing a UTF8-encoded copy of {@code text}
     */
    public static PluginMessage fromString(String channel, String text) {
        ByteBuf buf = Unpooled.buffer(5 + text.length());
        try {
            ByteBufUtils.writeUTF8(buf, text);
            byte[] array = buf.array();
            return new PluginMessage(channel, array);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING,
                "Error converting to PluginMessage: \"" + channel + "\", \"" + text + "\"", e);
        } finally {
            buf.release();
        }
        return null;
    }

}

