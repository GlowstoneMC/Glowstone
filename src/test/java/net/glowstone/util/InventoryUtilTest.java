package net.glowstone.util;

import net.glowstone.inventory.GlowInventory;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class InventoryUtilTest {
    @Test
    public void testEmptyStack() {
        ItemStack empty = InventoryUtil.createEmptyStack();
        Assert.assertTrue(InventoryUtil.isEmpty(empty));
    }

    @Test
    public void testNullStack() {
        ItemStack stack = null;
        Assert.assertTrue(InventoryUtil.isEmpty(InventoryUtil.itemOrEmpty(stack)));
    }

    @Test
    public void testItemOrEmpty() {
        ItemStack stack = new ItemStack(Material.STONE);
        Assert.assertTrue(InventoryUtil.itemOrEmpty(stack).equals(stack));
    }

    @Test
    public void testRandomItem() {
        Random random = new Random();
        Inventory inventory = new GlowInventory(null, InventoryType.CHEST);
        Assert.assertEquals(InventoryUtil.getRandomSlot(random, inventory, true), -1);
        Assert.assertTrue(InventoryUtil.getRandomSlot(random, inventory, false) >= 0);
        inventory.setItem(0, new ItemStack(Material.APPLE));
        Assert.assertEquals(InventoryUtil.getRandomSlot(random, inventory, true), 0);
        inventory.setItem(1, new ItemStack(Material.CARROT_ITEM));
        Assert.assertTrue(InventoryUtil.getRandomSlot(random, inventory, true) >= 0);
    }
}
