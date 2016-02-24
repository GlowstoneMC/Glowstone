package net.glowstone.generator.objects;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;

import java.util.*;
import java.util.Map.Entry;

public class RandomItemsContent {

    private final Map<RandomAmountItem, Integer> content = new LinkedHashMap<>();

    public void addItem(RandomAmountItem item, int weight) {
        content.put(item, weight);
    }

    public boolean fillContainer(Random random, DirectionalContainer container, BlockState state, int maxStacks) {
        if (state.getBlock().getState() instanceof InventoryHolder) {
            final Inventory inventory = ((InventoryHolder) state.getBlock().getState()).getInventory();
            final int size = inventory.getSize();
            for (int i = 0; i < maxStacks; i++) {
                final RandomAmountItem item = getRandomItem(random);
                if (item != null) {
                    for (ItemStack stack: item.getItemStacks(random)) {
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

        public Collection<ItemStack> getItemStacks(Random random) {
            int minAmount = stack.getAmount();
            int amount = random.nextInt(maxAmount - minAmount + 1) + minAmount;
            if (amount <= stack.getMaxStackSize()) {
                final ItemStack adjustedStack = stack.clone();
                adjustedStack.setAmount(amount);
                return Collections.unmodifiableList(Arrays.asList(adjustedStack));
            } else {
                final ItemStack[] stacks = new ItemStack[amount];
                for (int i = 0; i < amount; i++) {
                    stacks[i] = stack.clone();
                    stacks[i].setAmount(1);
                }
                return Collections.unmodifiableList(Arrays.asList(stacks));
            }
        }
    }
}
