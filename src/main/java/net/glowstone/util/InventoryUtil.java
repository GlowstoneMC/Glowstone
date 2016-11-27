package net.glowstone.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    /**
     * Checks whether the given ItemStack is empty.
     *
     * @param stack the ItemStack to check
     * @return whether the given ItemStack is empty
     */
    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

    /**
     * Converts the ItemStack to an empty ItemStack if the given ItemStack is null.
     *
     * @param stack the ItemStack to check and convert, if applicable
     * @return the converted ItemStack if applicable, the original ItemStack otherwise
     */
    public static ItemStack safeEmptyStack(ItemStack stack) {
        return stack == null ? createEmptyStack() : stack;
    }

    /**
     * Creates an empty ItemStack (Material.AIR * 0).
     *
     * @return an empty ItemStack
     */
    public static ItemStack createEmptyStack() {
        return new ItemStack(Material.AIR, 0);
    }
}
