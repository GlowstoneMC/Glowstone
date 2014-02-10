package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

public class CreativeItemMessage implements Message {

    private final int slot;
    private final ItemStack item;

    public CreativeItemMessage(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "CreativeItemMessage{" +
                "slot=" + slot +
                ", item=" + item +
                '}';
    }
}
