package net.glowstone;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Static methods used in multiple packages, but only by tests.
 */
public class TestUtils {

    private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

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

    /**
     * Returns an output that will, when returned from a {@link org.testng.annotations.DataProvider}
     * method, run the test on each instance of the given enum class.
     *
     * @param clazz an enum class
     * @return an Iterator with each value of the Enum wrapped in an Object[1]
     */
    public static Iterator<Object[]> enumAsDataProvider(Class<? extends Enum<?>> clazz) {
        return Stream.of(clazz.getEnumConstants()).map(x -> new Object[]{x}).iterator();
    }

    /**
     * Returns an output that will, when returned from a {@link org.testng.annotations.DataProvider}
     * method, run the test on each static final field of the given class whose type exactly matches
     * another given class.
     *
     * @param sourceClass the class whose fields are to be returned
     * @param fieldType the type of field to return
     * @return an Iterator with each matching static final field wrapped in an Object[1]
     */
    public static Iterator<Object[]> staticFinalFieldsDataProvider(
            Class<?> sourceClass, Class<?> fieldType) {
        return Stream.of(sourceClass.getFields())
                .filter(field -> field.getType() == fieldType)
                .filter(field -> (field.getModifiers() & STATIC_FINAL) == STATIC_FINAL)
                .map(x -> new Object[]{x})
                .iterator();
    }
}
