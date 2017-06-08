package net.glowstone.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InventoryUtil {

    public static final ItemStack[] NO_ITEMS = new ItemStack[0];
    public static final ImmutableItemStack EMPTY_STACK = new ImmutableItemStack(Material.AIR, 0);

    /**
     * Checks whether the given ItemStack is empty.
     *
     * @param stack the ItemStack to check
     * @return whether the given ItemStack is empty
     */
    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.equals(EMPTY_STACK) || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

    /**
     * Converts the ItemStack to an empty ItemStack if the given ItemStack is null.
     *
     * @param stack the ItemStack to check and convert, if applicable
     * @return the converted ItemStack if applicable, the original ItemStack otherwise
     */
    public static ItemStack itemOrEmpty(ItemStack stack) {
        return stack == null ? createEmptyStack() : stack;
    }

    /**
     * Creates an empty ItemStack (Material.AIR * 0).
     *
     * @return an empty ItemStack
     */
    public static ItemStack createEmptyStack() {
        return EMPTY_STACK;
    }

    /**
     * Get a random slot index in an Inventory.
     *
     * @param random      a Random instance
     * @param inventory   the inventory
     * @param ignoreEmpty whether to skip empty items in the inventory
     * @return the index of a random slot in the inventory, -1 if no possible slot was found
     */
    public static int getRandomSlot(Random random, Inventory inventory, boolean ignoreEmpty) {
        if (!ignoreEmpty) {
            return random.nextInt(inventory.getSize());
        }
        List<Integer> notEmpty = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!isEmpty(inventory.getItem(i))) {
                notEmpty.add(i);
            }
        }
        if (notEmpty.isEmpty()) {
            return -1;
        }
        return notEmpty.get(random.nextInt(notEmpty.size()));
    }
}
