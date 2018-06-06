package net.glowstone.inventory;

import static org.testng.AssertJUnit.assertEquals;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GlowInventoryTest {

    private GlowInventory inventory;

    @BeforeMethod
    public void setup() {
        inventory = new GlowInventory(null, InventoryType.CHEST);
    }

    @Test
    void testConsumeItem() {
        inventory.setItem(1, new ItemStack(Material.SAND, 55));

        assertEquals(1, inventory.consumeItem(1));
        assertEquals(54, inventory.getItem(1).getAmount());

        assertEquals(1, inventory.consumeItem(1, false));
        assertEquals(53, inventory.getItem(1).getAmount());

        assertEquals(53, inventory.consumeItem(1, true));
        assertEquals(0, inventory.getItem(1).getAmount());
    }
}
