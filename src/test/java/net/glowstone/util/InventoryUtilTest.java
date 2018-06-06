package net.glowstone.util;

import static org.hamcrest.CoreMatchers.is;
import static org.testng.AssertJUnit.assertThat;

import java.util.Random;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hamcrest.number.OrderingComparison;
import org.testng.annotations.Test;

public class InventoryUtilTest {

    @Test
    public void testEmptyStack() {
        ItemStack empty = InventoryUtil.createEmptyStack();
        assertThat(InventoryUtil.isEmpty(empty), is(true));
    }

    @Test
    public void testNullStack() {
        ItemStack stack = null;
        assertThat(InventoryUtil.isEmpty(InventoryUtil.itemOrEmpty(stack)), is(true));
    }

    @Test
    public void testItemOrEmpty() {
        ItemStack stack = new ItemStack(Material.STONE);
        assertThat(InventoryUtil.itemOrEmpty(stack), is(stack));
    }

    @Test
    public void testRandomItem() {
        Random random = new Random();
        Inventory inventory = new GlowInventory(null, InventoryType.CHEST);
        assertThat(-1, is(InventoryUtil.getRandomSlot(random, inventory, true)));
        assertThat(InventoryUtil.getRandomSlot(random, inventory, false),
            OrderingComparison.greaterThanOrEqualTo(0));
        inventory.setItem(0, new ItemStack(Material.APPLE));
        assertThat(0, is(InventoryUtil.getRandomSlot(random, inventory, true)));
        inventory.setItem(1, new ItemStack(Material.CARROT_ITEM));
        assertThat(InventoryUtil.getRandomSlot(random, inventory, true),
            OrderingComparison.greaterThanOrEqualTo(0));
    }
}
