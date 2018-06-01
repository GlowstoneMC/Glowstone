package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class EntityEquipmentMessage implements Message {

    public static final int HELD_ITEM = 0;
    public static final int OFF_HAND = 1;
    public static final int BOOTS_SLOT = 2;
    public static final int LEGGINGS_SLOT = 3;
    public static final int CHESTPLATE_SLOT = 4;
    public static final int HELMET_SLOT = 5;

    private final int id;
    private final int slot;
    private final ItemStack stack;

}
