package net.glowstone;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;

/**
 * Static methods used in multiple packages, but only by tests.
 */
public class TestUtils {

    /**
     * Asserts that exactly the expected number of items matching a predicate are in the given
     * inventory, summed across all stacks.
     *
     * @param inventory the inventory to scan
     * @param expectedMatches the number of matching items to expect
     * @param filter the predicate that determines which items match
     */
    public static void checkInventory(Inventory inventory, int expectedMatches,
            Predicate<ItemStack> filter) {
        int matches = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && filter.test(item)) {
                matches += item.getAmount();
            }
        }
        /* TODO once ItemStack.toString() is fixed for tests:
        assertEquals(
                String.format("Expected exactly %d items but found %d, matching {%s} against:%n%s",
                        expectedMatches, matches, filter, inventory), expectedMatches, matches);
        */
        assertEquals(
                String.format("Expected exactly %d items but found %d, matching {%s}",
                        expectedMatches, matches, filter), expectedMatches, matches);
    }

    /**
     * Returns a {@link Predicate} matching items of one specific type, with a friendly {@link
     * #toString()} implementation.
     *
     * @param type the type to match
     * @return a Predicate for matching items
     */
    public static Predicate<ItemStack> itemTypeMatcher(final Material type) {
        return new Predicate<ItemStack>() {
            @Override
            public boolean test(ItemStack item) {
                return item.getType() == type;
            }
            @Override
            public String toString() {
                return "items of type " + type;
            }
        };
    }

    /**
     * Returns a {@link Predicate} matching items of a collection of specific types, with a friendly
     * {@link #toString()} implementation.
     *
     * @param types the types to match any of
     * @return a Predicate for matching items
     */
    public static Predicate<ItemStack> itemTypeMatcher(final Collection<Material> types) {
        return new Predicate<ItemStack>() {
            @Override
            public boolean test(ItemStack item) {
                return types.contains(item.getType());
            }
            @Override
            public String toString() {
                return "items of types " + types;
            }
        };
    }
}
