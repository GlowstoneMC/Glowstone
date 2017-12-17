package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.inventory.GlowEnchantingInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.EnchantItemMessage;
import org.bukkit.inventory.Inventory;

public final class EnchantItemHandler implements MessageHandler<GlowSession, EnchantItemMessage> {

    @Override
    public void handle(GlowSession session, EnchantItemMessage message) {
        Inventory view = session.getPlayer().getOpenInventory().getTopInventory();
        if (view instanceof GlowEnchantingInventory) {
            ((GlowEnchantingInventory) view).onPlayerEnchant(message.getEnchantment());
        } else {
            GlowServer.logger.info("Player " + session.getPlayer().getName()
                + " tried to enchant item while no enchanting inventory was open!");
        }
    }
}
