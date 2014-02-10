package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

public final class SetWindowSlotMessage implements Message {

    private final int id, slot;
    private final ItemStack item;

    public SetWindowSlotMessage(int id, int slot, ItemStack item) {
        this.id = id;
        this.slot = slot;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "SetWindowSlotMessage{" +
                "id=" + id +
                ", slot=" + slot +
                ", item=" + item +
                '}';
    }
}
