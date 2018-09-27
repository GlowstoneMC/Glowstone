package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.CraftRecipeRequestMessage;

public final class CraftRecipeRequestHandler implements
    MessageHandler<GlowSession, CraftRecipeRequestMessage> {

    @Override
    public void handle(GlowSession session, CraftRecipeRequestMessage message) {
        ConsoleMessages.Warn.Net.CRAFTING_RECIPE_UNSUPPORTED.log(session.getPlayer().getName());
    }
}
