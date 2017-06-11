package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.AdvancementTabMessage;

public class AdvancementTabHandler implements MessageHandler<GlowSession, AdvancementTabMessage> {
    @Override
    public void handle(GlowSession session, AdvancementTabMessage message) {
        if (message.getAction() == AdvancementTabMessage.ACTION_CLOSE) {
            System.out.println("Received AdvancementTabHandler (close)");
        } else {
            System.out.println("Received AdvancementTabHandler (open:" + message.getTabId() + ")");
        }
    }
}
