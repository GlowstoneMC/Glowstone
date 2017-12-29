package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemProjectile;
import net.glowstone.block.itemtype.ItemTimedUsage;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.UseItemMessage;
import net.glowstone.util.InventoryUtil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseItemHandler implements MessageHandler<GlowSession, UseItemMessage> {
    @Override
    public void handle(GlowSession session, UseItemMessage message) {
        ItemStack holding = session.getPlayer().getInventory().getItemInMainHand();
        if (message.getHand() == UseItemMessage.OFF_HAND) {
            holding = session.getPlayer().getInventory().getItemInOffHand();
        }
        if (holding == null) {
            return;
        }
        if (!InventoryUtil.isEmpty(holding)) {
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (type != null) {
                if (type.getContext() == Context.AIR || type.getContext() == Context.ANY) {
                    type.rightClickAir(player, holding);
                } else {
                    if ((holding.getType() == Material.WATER_BUCKET
                        || holding.getType() == Material.LAVA_BUCKET) 
                            && player.getGameMode() != GameMode.CREATIVE) {
                        holding.setType(Material.BUCKET);
                    } else if (type instanceof ItemTimedUsage) {
            	        ((ItemTimedUsage) type).startUse(session.getPlayer(), holding);
                    } else if (type instanceof ItemProjectile) {
                        ((ItemProjectile) type).use(session.getPlayer(), holding);
                    }
                }
            }

            // Empties the user's inventory when the item is used up
            if (holding.getAmount() <= 0) {
                holding = InventoryUtil.createEmptyStack();
            }
            player.getInventory().setItem(message.getEquipmentSlot(), holding);
        }
    }
}
