package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.CraftingBookDataMessage;

public final class CraftingBookDataHandler implements
    MessageHandler<GlowSession, CraftingBookDataMessage> {

    @Override
    public void handle(GlowSession session, CraftingBookDataMessage message) {
        GlowPlayer player = session.getPlayer();
        if (message.getType() == CraftingBookDataMessage.TYPE_STATUS) {
            player.getRecipeMonitor().setBookOpen(message.isBookOpen());
            player.getRecipeMonitor().setFilterCraftable(message.isFilter());
            return;
        }
        ConsoleMessages.Warn.Net.CRAFTING_BOOK_UNSUPPORTED.log(session.getPlayer().getName(),
                message);
    }
}
