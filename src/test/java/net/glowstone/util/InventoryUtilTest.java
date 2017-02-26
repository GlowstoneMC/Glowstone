package net.glowstone.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;

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
}
