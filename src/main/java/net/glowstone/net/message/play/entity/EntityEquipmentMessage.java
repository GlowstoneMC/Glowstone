package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

public final class EntityEquipmentMessage implements Message {
    
    public static final int HELD_ITEM = 0;
    public static final int BOOTS_SLOT = 1;
    public static final int LEGGINGS_SLOT = 2;
    public static final int CHESTPLATE_SLOT = 3;
    public static final int HELMET_SLOT = 4;

    private final int id, slot;
    private final ItemStack stack;

    public EntityEquipmentMessage(int id, int slot, ItemStack stack) {
        this.id = id;
        this.slot = slot;
        this.stack = stack;
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public String toString() {
        return "EntityEquipmentMessage{" +
                "id=" + id +
                ", slot=" + slot +
                ", stack=" + stack +
                '}';
    }
}
