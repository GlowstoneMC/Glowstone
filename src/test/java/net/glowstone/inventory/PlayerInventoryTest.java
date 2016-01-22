package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for GlowPlayerInventory.
 */
public class PlayerInventoryTest {

    private static final int SIZE = 36;

    private static final ItemStack TEST_BOOTS = new ItemStack(Material.DIAMOND_BOOTS);
    private static final ItemStack TEST_LEGGINGS = null;
    private static final ItemStack TEST_CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
    private static final ItemStack TEST_HELMET = new ItemStack(Material.IRON_HELMET);
    private static final ItemStack[] TEST_ARMOR = new ItemStack[] {
            TEST_BOOTS, TEST_LEGGINGS, TEST_CHESTPLATE, TEST_HELMET
    };

    private GlowPlayerInventory inventory;

    @Before
    public void setup() {
        inventory = new GlowPlayerInventory(null);
    }

    @Test
    public void testBasics() {
        assertEquals("Inventory was wrong size", SIZE, inventory.getSize());
        assertEquals("Contents were wrong size", SIZE, inventory.getContents().length);
        assertEquals("Type was wrong", InventoryType.PLAYER, inventory.getType());
    }

    @Test
    public void testSlotTypes() {
        for (int i = 0; i < 9; ++i) {
            assertEquals("Slot type for " + i + " was wrong", SlotType.QUICKBAR, inventory.getSlotType(i));
        }
        for (int i = 9; i < SIZE; ++i) {
            assertEquals("Slot type for " + i + " was wrong", SlotType.CONTAINER, inventory.getSlotType(i));
        }
        for (int i = SIZE; i < SIZE + 4; ++i) {
            assertEquals("Slot type for " + i + " was wrong", SlotType.ARMOR, inventory.getSlotType(i));
        }
    }

    /**
     * Check that the armor contents match the test armor contents.
     */
    private void checkArmorContents() {
        assertArrayEquals("Mismatch in armor contents", TEST_ARMOR, inventory.getArmorContents());
        assertEquals("Mismatch in boots slot", TEST_BOOTS, inventory.getBoots());
        assertEquals("Mismatch in leggings slot", TEST_LEGGINGS, inventory.getLeggings());
        assertEquals("Mismatch in chestplate slot", TEST_CHESTPLATE, inventory.getChestplate());
        assertEquals("Mismatch in helmet slot", TEST_HELMET, inventory.getHelmet());
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
        assertEquals("Wrong boots drop chance", 1, inventory.getBootsDropChance(), 0.001);
        assertEquals("Wrong leggings drop chance", 1, inventory.getLeggingsDropChance(), 0.001);
        assertEquals("Wrong chestplate drop chance", 1, inventory.getChestplateDropChance(), 0.001);
        assertEquals("Wrong helmet drop chance", 1, inventory.getHelmetDropChance(), 0.001);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetDropChance() {
        inventory.setChestplateDropChance(0.5f);
    }

}
