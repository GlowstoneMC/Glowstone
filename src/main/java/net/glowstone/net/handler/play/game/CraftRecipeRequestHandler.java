package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.CraftRecipeRequestMessage;

public final class CraftRecipeRequestHandler implements
    MessageHandler<GlowSession, CraftRecipeRequestMessage> {

    @Override
    public void handle(GlowSession session, CraftRecipeRequestMessage message) {
        // TODO: Support crafting recipe book
        session.getServer().getLogger().warning(
            "Received craft recipe request from " + session.getPlayer().getName()
                + ", not currently supported.");
    }
}
