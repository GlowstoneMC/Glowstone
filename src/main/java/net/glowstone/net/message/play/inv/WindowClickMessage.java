package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

public final class WindowClickMessage implements Message {

    private final int id, slot, button, transaction, mode;
    private final ItemStack item;

    public WindowClickMessage(int id, int slot, int button, int transaction, int mode, ItemStack item) {
        this.id = id;
        this.slot = slot;
        this.button = button;
        this.transaction = transaction;
        this.mode = mode;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public int getButton() {
        return button;
    }

    public int getTransaction() {
        return transaction;
    }

    public int getMode() {
        return mode;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "WindowClickMessage{" +
                "id=" + id +
                ", slot=" + slot +
                ", button=" + button +
                ", transaction=" + transaction +
                ", mode=" + mode +
                ", item=" + item +
                '}';
    }
}
