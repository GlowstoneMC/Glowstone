package net.glowstone.msg.handler;

import java.util.logging.Level;

import org.bukkit.ChatColor;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.ChatMessage;
import net.glowstone.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, ChatMessage message) {
		if (player == null)
			return;

		String text = message.getMessage();
        text = text.trim();
        
		if (text.length() > 100) {
			session.disconnect("Chat message too long.");
		} else if (text.startsWith("/")) {
			try {
                if (!player.performCommand(text.substring(1))) {
                    player.sendMessage(ChatColor.RED + "An error occurred while executing your command.");
                }
            }
            catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "An exception occured while executing your command.");
                GlowServer.logger.log(Level.SEVERE, "Error while executing command: {0}", ex.getMessage());
                ex.printStackTrace();
            }
		} else {
            player.getServer().broadcastMessage("<" + player.getName() + "> " + text);
			player.getWorld().broadcastMessage("[" + player.getWorld().getName() + "]<" + player.getName() + "> " + text);
            GlowServer.logger.log(Level.INFO, "<{0}> {1}", new Object[]{player.getName(), text});
		}
	}

}
