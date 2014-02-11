package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class SetWindowContentsMessage implements Message {

    private final int id;
    private final ItemStack[] items;

    public SetWindowContentsMessage(int id, ItemStack[] items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public ItemStack[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SetWindowContentsMessage{id=" + id + ",slots=" + Arrays.toString(items) + "}";
    }
}
