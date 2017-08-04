package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.CraftingBookDataMessage;

public final class CraftingBookDataHandler implements MessageHandler<GlowSession, CraftingBookDataMessage> {
    @Override
    public void handle(GlowSession session, CraftingBookDataMessage message) {
        // TODO: Support crafting book data
        session.getServer().getLogger().warning("Received crafting book data from " + session.getPlayer().getName() + ", not currently supported.");
    }
}
