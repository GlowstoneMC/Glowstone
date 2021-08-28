package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public final class PluginMessageHandler implements MessageHandler<GlowSession, PluginMessage> {

    @Override
    public void handle(GlowSession session, PluginMessage message) {
        String channel = message.getChannel();

        // register and unregister: NUL-separated list of channels

        if (channel.equals("minecraft:register")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " registered channel: " + regChannel);
                session.getPlayer().addChannel(regChannel);
            }
        } else if (channel.equals("minecraft:unregister")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " unregistered channel: " + regChannel);
                session.getPlayer().removeChannel(regChannel);
            }
        } else if (channel.startsWith("minecraft:")) {
            // internal Minecraft channels
            handleInternal(session, channel, message.getData());
        } else {
            session.getServer().getMessenger()
                .dispatchIncomingMessage(session.getPlayer(), channel, message.getData());
        }
    }

    private void handleInternal(GlowSession session, String channel, byte... data) {
        ByteBuf buf = null;
        try {
            buf = Unpooled.wrappedBuffer(data);
            switch (channel) {
                case "minecraft:brand":
                    // vanilla server doesn't handle this, for now just log it
                    String brand = null;
                    try {
                        brand = ByteBufUtils.readUTF8(buf);
                    } catch (IOException e) {
                        GlowServer.logger
                            .log(Level.WARNING, "Error reading client brand of " + session, e);
                    }
                    if (brand != null && !brand.equals("vanilla")) {
                        GlowServer.logger
                            .info("Client brand of " + session.getPlayer().getName() + " is: "
                                + brand);
                    }
                    break;
                default:
                    GlowServer.logger.info(session + " used unknown Minecraft channel: " + channel);
                    break;
            }
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    private String string(byte... data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
