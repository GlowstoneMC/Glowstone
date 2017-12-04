package net.glowstone.inventory;

import java.util.Arrays;
import java.util.List;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.InventoryUtil;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

public class GlowInventorySlot {

    private static final SlotType DEFAULT_TYPE = SlotType.CONTAINER;

    private ItemStack item;
    private SlotType type;

    public GlowInventorySlot() {
        this(DEFAULT_TYPE);
    }

    public GlowInventorySlot(SlotType type) {
        this(InventoryUtil.createEmptyStack(), type);
    }

    public GlowInventorySlot(ItemStack item) {
        this(item, DEFAULT_TYPE);
    }

    public GlowInventorySlot(ItemStack item, SlotType type) {
        this.item = item;
        this.type = type;
    }

    public static GlowInventorySlot[] createArray(int len) {
        GlowInventorySlot[] result = new GlowInventorySlot[len];

        for (int i = 0; i < len; i++) {
            result[i] = new GlowInventorySlot();
        }

        return result;
    }

    public static List<GlowInventorySlot> createList(int len) {
        return Arrays.asList(createArray(len));
    }

    public ItemStack getItem() {
        return InventoryUtil.itemOrEmpty(item);
    }

    public void setItem(ItemStack item) {
        this.item = ItemIds.sanitize(item);
    }

    public SlotType getType() {
        return type;
    }

    public void setType(SlotType type) {
        this.type = type;
    }
}
