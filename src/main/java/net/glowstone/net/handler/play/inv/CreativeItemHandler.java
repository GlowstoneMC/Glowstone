package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.constants.ItemIds;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class CreativeItemHandler implements MessageHandler<GlowSession, CreativeItemMessage> {

    @Override
    public void handle(GlowSession session, CreativeItemMessage message) {
        GlowPlayer player = session.getPlayer();
        // CraftBukkit does use a inventory view with both inventories set to the player's inventory
        // for the creative inventory as there is no second inventory (no crafting) visible for the
        // client
        InventoryView view = player.getOpenInventory();

        // only if creative mode; only if default (player) inventory
        if (player.getGameMode() != GameMode.CREATIVE
            || !GlowInventoryView.isDefault(player.getOpenInventory())) {
            player.kickPlayer(GlowstoneMessages.Kick.CREATIVE_ITEM.get());
            return;
        }
        ItemStack stack = ItemIds.sanitize(message.getItem());

        // clicking outside drops the item
        EventFactory eventFactory = EventFactory.getInstance();
        if (message.getSlot() < 0) {
            InventoryCreativeEvent event = eventFactory
                .callEvent(new InventoryCreativeEvent(view, SlotType.OUTSIDE, -999, stack));
            if (event.isCancelled()) {
                session.send(new SetWindowSlotMessage(-1, -1, stack));
            } else {
                player.drop(event.getCursor());
            }
            return;
        }
        int viewSlot = message.getSlot();

        // if the content hasn't changed, ignore the message
        // this happens quiet often as the client tends to update the whole inventory at once
        if (Objects.equals(stack, view.getItem(viewSlot))) {
            return;
        }
        GlowInventory inv = player.getInventory();
        int slot = view.convertSlot(viewSlot);
        SlotType type = inv.getSlotType(slot);
        InventoryCreativeEvent event = eventFactory
            .callEvent(new InventoryCreativeEvent(view, type, viewSlot, stack));
        if (event.isCancelled()) {
            // send original slot to player to prevent async inventories
            player.sendItemChange(viewSlot, view.getItem(viewSlot));
            // don't keep track of player's current item, just give them back what they tried to
            // place
            session.send(new SetWindowSlotMessage(-1, -1, stack));
            return;
        }

        view.setItem(viewSlot, stack);
    }
}
