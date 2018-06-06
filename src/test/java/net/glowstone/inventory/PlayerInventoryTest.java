package net.glowstone.inventory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertEquals;

import net.glowstone.testutils.ServerShim;
import net.glowstone.util.IsFloatCloseTo;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for GlowPlayerInventory.
 */
public class PlayerInventoryTest {

    private static final int SIZE = 41;

    private static final ItemStack TEST_BOOTS = new ItemStack(Material.DIAMOND_BOOTS);
    private static final ItemStack TEST_LEGGINGS = new ItemStack(Material.AIR, 0);
    private static final ItemStack TEST_CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
    private static final ItemStack TEST_HELMET = new ItemStack(Material.IRON_HELMET);
    private static final ItemStack TEST_MAIN_HAND = new ItemStack(Material.SEEDS, 64);
    private static final ItemStack[] TEST_ARMOR = new ItemStack[]{
        TEST_BOOTS, TEST_LEGGINGS, TEST_CHESTPLATE, TEST_HELMET
    };

    private GlowPlayerInventory inventory;

    @BeforeClass
    public static void initShim() {
        ServerShim.install();
    }

    @BeforeMethod
    public void setup() {
        inventory = new GlowPlayerInventory(null);
    }

    @Test
    public void testBasics() {
        assertThat("Inventory was wrong size", inventory.getSize(), is(SIZE));
        assertThat("Contents were wrong size", inventory.getContents().length, is(SIZE));
        assertThat("Type was wrong", inventory.getType(), is(InventoryType.PLAYER));
    }

    @Test
    public void testSlotTypes() {
        for (int i = 0; i < 9; ++i) {
            assertThat("Slot type for " + i + " was wrong", inventory.getSlotType(i),
                is(SlotType.QUICKBAR));
        }
        for (int i = 9; i < SIZE - 5; ++i) {
            assertThat("Slot type for " + i + " was wrong", inventory.getSlotType(i),
                is(SlotType.CONTAINER));
        }
        for (int i = SIZE - 4; i < SIZE - 1; ++i) {
            assertThat("Slot type for " + i + " was wrong", inventory.getSlotType(i),
                is(SlotType.ARMOR));
        }
        assertThat("Slot type for offhand (40) was wrong", inventory.getSlotType(40),
            is(SlotType.CONTAINER));
    }

    /**
     * Check that the armor contents match the test armor contents.
     */
    private void checkArmorContents() {
        assertThat("Mismatch in armor contents", inventory.getArmorContents(), is(TEST_ARMOR));
        assertThat("Mismatch in boots slot", inventory.getBoots(), is(TEST_BOOTS));
        assertThat("Mismatch in leggings slot", inventory.getLeggings(), is(TEST_LEGGINGS));
        assertThat("Mismatch in chestplate slot", inventory.getChestplate(), is(TEST_CHESTPLATE));
        assertThat("Mismatch in helmet slot", inventory.getHelmet(), is(TEST_HELMET));
    }

    @Test
    public void testSetArmorContents() {
        inventory.setArmorContents(TEST_ARMOR);
        checkArmorContents();
    }

    @Test
    public void testSetArmorSlots() {
        inventory.setBoots(TEST_BOOTS);
        inventory.setLeggings(TEST_LEGGINGS);
        inventory.setChestplate(TEST_CHESTPLATE);
        inventory.setHelmet(TEST_HELMET);
        checkArmorContents();
    }

    @Test
    public void testDropChance() {
        assertThat("Wrong boots drop chance", inventory.getBootsDropChance(),
            IsFloatCloseTo.closeTo(1f, 0.001f));
        assertThat("Wrong leggings drop chance", inventory.getLeggingsDropChance(),
            IsFloatCloseTo.closeTo(1f, 0.001f));
        assertThat("Wrong chestplate drop chance", inventory.getChestplateDropChance(),
            IsFloatCloseTo.closeTo(1f, 0.001f));
        assertThat("Wrong helmet drop chance", inventory.getHelmetDropChance(),
            IsFloatCloseTo.closeTo(1f, 0.001f));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testSetDropChance() {
        inventory.setChestplateDropChance(0.5f);
    }

    @Test
    void testConsumeItemInMainHand() {
        inventory.setItemInMainHand(TEST_MAIN_HAND);

        assertEquals(1, inventory.consumeItemInMainHand());
        assertEquals(63, inventory.getItemInMainHand().getAmount());

        assertEquals(1, inventory.consumeItemInMainHand(false));
        assertEquals(62, inventory.getItemInMainHand().getAmount());

        assertEquals(62, inventory.consumeItemInMainHand(true));
        assertEquals(0, inventory.getItemInMainHand().getAmount());
    }
}
