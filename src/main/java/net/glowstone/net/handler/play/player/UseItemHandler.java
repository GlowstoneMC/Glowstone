package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.UseItemMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UseItemHandler implements MessageHandler<GlowSession, UseItemMessage> {
    @Override
    public void handle(GlowSession session, UseItemMessage message) {
        GlowPlayer player = session.getPlayer();
        ItemStack holding = player.getItemInHand();

        if (holding != null) {
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (type != null) {
                if (type.canOnlyUseSelf()) {
                    type.rightClickAir(player, holding);
                } else {
                    if (holding.getType() == Material.WATER_BUCKET || holding.getType() == Material.LAVA_BUCKET) {
                        holding.setType(Material.BUCKET);
                    }
                }
            }
        }
    }
}
