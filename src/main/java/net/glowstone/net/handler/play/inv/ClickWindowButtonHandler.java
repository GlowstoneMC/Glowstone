package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.inventory.GlowEnchantingInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.ClickWindowButtonMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.inventory.StonecutterInventory;

public final class ClickWindowButtonHandler implements MessageHandler<GlowSession, ClickWindowButtonMessage> {

    @Override
    public void handle(GlowSession session, ClickWindowButtonMessage message) {
        Inventory view = session.getPlayer().getOpenInventory().getTopInventory();
        if (view instanceof GlowEnchantingInventory) {
            ((GlowEnchantingInventory) view).onPlayerEnchant(message.getButton());
        } else if (view instanceof LecternInventory) {
            throw new UnsupportedOperationException("Lectern are not supported yet.");
        } else if (view instanceof StonecutterInventory) {
            throw new UnsupportedOperationException("Stonecutters are not supported yet.");
        } else if (view instanceof LoomInventory) {
            throw new UnsupportedOperationException("Looms are not supported yet.");
        } else {
            ConsoleMessages.Info.Net.WINDOW_NOT_OPEN.log(session.getPlayer().getName());
        }
    }
}
