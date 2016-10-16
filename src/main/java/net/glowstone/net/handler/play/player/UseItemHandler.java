package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemProjectile;
import net.glowstone.block.itemtype.ItemTimedUsage;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.UseItemMessage;
import org.bukkit.entity.Projectile;
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
        ItemType type = ItemTable.instance().getItem(holding.getType());
        if (type == null) {
            return;
        }
        if (type instanceof ItemTimedUsage) {
            ((ItemTimedUsage) type).startUse(session.getPlayer(), holding);
        } else if (type instanceof ItemProjectile) {
            Projectile projectile = ((ItemProjectile) type).use(session.getPlayer(), holding);
        }
    }
}
