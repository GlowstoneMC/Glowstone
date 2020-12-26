package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.DisplayedRecipeMessage;

public final class DisplayedRecipeHandler implements MessageHandler<GlowSession, DisplayedRecipeMessage> {
    @Override
    public void handle(GlowSession session, DisplayedRecipeMessage message) {
        // TODO: Support crafting book properly.
        ConsoleMessages.Warn.Net.CRAFTING_BOOK_UNSUPPORTED.log(session.getPlayer().getName(),
                message);
    }
}
