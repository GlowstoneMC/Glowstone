package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;

import java.nio.charset.StandardCharsets;

public final class PluginMessageHandler implements MessageHandler<GlowSession, PluginMessage> {
    public void handle(GlowSession session, PluginMessage message) {
        final String channel = message.getChannel();

        // register and unregister: NUL-separated list of channels

        if (channel.equals("REGISTER")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " registered channel: " + regChannel);
                session.getPlayer().addChannel(regChannel);
            }
        } else if (channel.equals("UNREGISTER")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " unregistered channel: " + regChannel);
                session.getPlayer().removeChannel(regChannel);
            }
        } else if (channel.startsWith("MC|")) {
            // internal Minecraft channels
            handleInternal(session, channel, message.getData());
        } else {
            session.getServer().getMessenger().dispatchIncomingMessage(session.getPlayer(), channel, message.getData());
        }
    }

    private void handleInternal(GlowSession session, String channel, byte[] data) {
        //ByteBuf buf = Unpooled.wrappedBuffer(data);
        /*
        MC|Brand
            entire data: string of client's brand (e.g. "vanilla")
        MC|BEdit
            item stack: new book item (should be verified)
        MC|BSign
            item stack: new book item (should be verified)
        MC|TrSel
            int: villager trade to select
        MC|AdvCdm
            byte: mode
            if 0:
                int x, int y, int z (command block in world)
            if 1:
                int entity (command block minecart)
            string: command to set
        MC|Beacon
            two ints, presumably the selected enchants
        MC|ItemName
            entire data: name to apply to item in anvil
         */

        if (channel.equals("MC|Brand")) {
            // vanilla server doesn't handle this, for now just log it
            GlowServer.logger.info("Client brand of " + session.getPlayer().getName() + " is: " + string(data));
        } else {
            GlowServer.logger.info(session + " used unknown Minecraft channel: " + channel);
        }
    }

    private String string(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
