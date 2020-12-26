package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.RecipeBookStateMessage;

public final class RecipeBookStateHandler implements
        MessageHandler<GlowSession, RecipeBookStateMessage> {

    @Override
    public void handle(GlowSession session, RecipeBookStateMessage message) {
        GlowPlayer player = session.getPlayer();
        RecipeBookStateMessage.RecipeBookType book = message.getBook(); // TODO: Use this field.
        player.getRecipeMonitor().setBookOpen(message.isBookOpen());
        player.getRecipeMonitor().setFilterCraftable(message.isFilterOpen());
    }
}
