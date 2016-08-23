package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.constants.ItemIds;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.CreativeItemPacket;
import net.glowstone.net.message.play.inv.WindowSlotPacket;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class CreativeItemHandler implements MessageHandler<GlowSession, CreativeItemPacket> {
    @Override
    public void handle(GlowSession session, CreativeItemPacket message) {
        GlowPlayer player = session.getPlayer();
        GlowInventory inv = player.getInventory();
        // CraftBukkit does use a inventory view with both inventories set to the player's inventory
        // for the creative inventory as there is no second inventory (no crafting) visible for the client
        InventoryView view = player.getOpenInventory();
        int viewSlot = message.getSlot();
        int slot = view.convertSlot(viewSlot);
        ItemStack stack = ItemIds.sanitize(message.getItem());
        SlotType type = inv.getSlotType(slot);

        // only if creative mode
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.kickPlayer("Illegal creative mode item selection");
            return;
        }

        // only if default (player) inventory
        if (!GlowInventoryView.isDefault(player.getOpenInventory())) {
            player.kickPlayer("Illegal creative mode item selection");
            return;
        }

        // clicking outside drops the item
        if (message.getSlot() < 0) {
            InventoryCreativeEvent event = EventFactory.callEvent(new InventoryCreativeEvent(view, SlotType.OUTSIDE, -999, stack));
            if (event.isCancelled()) {
                session.send(new WindowSlotPacket(-1, -1, stack));
            } else {
                player.drop(event.getCursor());
            }
            return;
        }

        // if the content hasn't changed, ignore the message
        // this happens quiet often as the client tends to update the whole inventory at once
        if (Objects.equals(stack, view.getItem(viewSlot))) {
            return;
        }

        InventoryCreativeEvent event = EventFactory.callEvent(new InventoryCreativeEvent(view, type, viewSlot, stack));
        if (event.isCancelled()) {
            // send original slot to player to prevent async inventories
            player.sendItemChange(viewSlot, view.getItem(viewSlot));
            // don't keep track of player's current item, just give them back what they tried to place
            session.send(new WindowSlotPacket(-1, -1, stack));
            return;
        }

        view.setItem(viewSlot, stack);
    }
}
