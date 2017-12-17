package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.inventory.ItemStack;

public class ItemWrittenBook extends ItemType {

    private static final byte[] EMPTY = new byte[0];

    @Override
    public Context getContext() {
        return Context.AIR;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        openBook(player);
    }

    private void openBook(GlowPlayer player) {
        player.getSession().send(new PluginMessage("MC|BOpen", EMPTY));
    }
}
