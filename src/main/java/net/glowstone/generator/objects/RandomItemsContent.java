package net.glowstone.generator.objects;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class RandomItemsContent {

    private final Map<RandomAmountItem, Integer> content = new LinkedHashMap<>();

    public void addItem(RandomAmountItem item, int weight) {
        content.put(item, weight);
    }

    /**
     * Populates a container with random items.
     *
     * @param random the PRNG to use
     * @param state the block state for a container block
     * @param maxStacks the maximum number of slots to fill
     * @return true if successful (currently always true)
     */
    public boolean fillContainer(Random random, BlockState state, int maxStacks) {
        if (state.getBlock().getState() instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) state.getBlock().getState()).getInventory();
            int size = inventory.getSize();
            for (int i = 0; i < maxStacks; i++) {
                RandomAmountItem item = getRandomItem(random);
                if (item != null) {
                    for (ItemStack stack : item.getItemStacks(random)) {
                        // slot can be overriden hence maxStacks can be less than what's expected
                        inventory.setItem(random.nextInt(size), stack);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Bad container type");
        }

        return true;
    }

    /**
     * Choose a random {@link RandomAmountItem}.
     *
     * @param random the PRNG to use
     * @return the random item
     */
    public RandomAmountItem getRandomItem(Random random) {
        int totalWeight = 0;
        for (int i : content.values()) {
            totalWeight += i;
        }
        if (totalWeight <= 0) {
            return null;
        }
        int weight = random.nextInt(totalWeight);
        for (Entry<RandomAmountItem, Integer> entry : content.entrySet()) {
            weight -= entry.getValue();
            if (weight < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static class RandomAmountItem {

        private final int maxAmount;
        private final ItemStack stack;

        public RandomAmountItem(Material type, int minAmount, int maxAmount) {
            this(type, 0, minAmount, maxAmount);
        }

        public RandomAmountItem(Material type, int data, int minAmount, int maxAmount) {
            stack = new ItemStack(type, minAmount, (short) data);
            this.maxAmount = maxAmount;
        }

        /**
         * Generate a random set of items.
         *
         * @param random the PRNG to use
         * @return an immutable collection of randomly-generated items
         */
        public Collection<ItemStack> getItemStacks(Random random) {
            int minAmount = stack.getAmount();
            int amount = random.nextInt(maxAmount - minAmount + 1) + minAmount;
            if (amount <= stack.getMaxStackSize()) {
                ItemStack adjustedStack = stack.clone();
                adjustedStack.setAmount(amount);
                return Collections.unmodifiableList(Arrays.asList(adjustedStack));
            } else {
                ItemStack[] stacks = new ItemStack[amount];
                for (int i = 0; i < amount; i++) {
                    stacks[i] = stack.clone();
                    stacks[i].setAmount(1);
                }
                return Collections.unmodifiableList(Arrays.asList(stacks));
            }
        }
    }
}
