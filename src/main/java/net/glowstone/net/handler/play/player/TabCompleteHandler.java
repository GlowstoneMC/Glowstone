package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import org.bukkit.ChatColor;

public final class TabCompleteHandler implements MessageHandler<GlowSession, TabCompleteMessage> {
    public void handle(GlowSession session, TabCompleteMessage message) {
        // todo
        session.getPlayer().sendMessage(ChatColor.GRAY + "[TabComplete]: " + ChatColor.RESET + message.getText());
    }
}
