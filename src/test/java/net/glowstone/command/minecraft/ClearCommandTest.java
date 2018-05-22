package net.glowstone.command.minecraft;

import static net.glowstone.TestUtils.checkInventory;
import static net.glowstone.TestUtils.itemTypeMatcher;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.inventory.GlowPlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ClearCommandTest extends CommandTestWithFakePlayers {
    private GlowPlayerInventory inventory;

    public ClearCommandTest() {
        super("ChuckNorris");
    }

    @Override
    @Before
    public void before() {
        super.before();
        /* FIXME: Intended to prevent NPE in ItemStack.toString(), but causes failures: */
        when(Bukkit.getItemFactory()).thenReturn(GlowItemFactory.instance());
        /**/
        command = new ClearCommand();
        inventory = new GlowPlayerInventory(fakePlayers[0]);
        inventory.setItemInMainHand(new ItemStack(Material.DIRT, 32));
        inventory.setItemInOffHand(new ItemStack(Material.DIAMOND_AXE, 1, (short) 30));
        inventory.setItem(9, new ItemStack(Material.DIRT, 64));
        inventory.setItem(10, new ItemStack(Material.DIAMOND));
        inventory.setItem(11, new ItemStack(Material.DIAMOND_AXE));
        when(fakePlayers[0].getInventory()).thenReturn(inventory);
    }

    @Test
    public void testClearAll() {
        assertTrue(command.execute(opSender, "label", new String[]{"ChuckNorris"}));
        checkInventory(inventory, 0, item -> true); // should be empty
    }

    @Test
    public void testCountOnly() {
        assertTrue(command.execute(opSender, "label",
                new String[]{"ChuckNorris", "minecraft:diamond_axe", "-1", "0"}));
        Mockito.verify(opSender)
                .sendMessage(eq("ChuckNorris has 2 items that match the criteria"));
        checkInventory(inventory, 96, itemTypeMatcher(Material.DIRT));
        checkInventory(inventory, 2, itemTypeMatcher(Material.DIAMOND_AXE));
        checkInventory(inventory, 1, itemTypeMatcher(Material.DIAMOND));
    }

    @Test
    public void testClearSpecificItemAll() {
        assertTrue(command.execute(opSender, "label",
                new String[]{"ChuckNorris", "minecraft:diamond_axe", "-1", "-1"}));
        checkInventory(inventory, 96, itemTypeMatcher(Material.DIRT));
        checkInventory(inventory, 0, itemTypeMatcher(Material.DIAMOND_AXE));
        checkInventory(inventory, 1, itemTypeMatcher(Material.DIAMOND));
    }

    @Test
    public void testClearSpecificItemLimited() {
        assertTrue(command.execute(opSender, "label",
                new String[]{"ChuckNorris", "minecraft:dirt", "-1", "50"}));
        checkInventory(inventory, 46, itemTypeMatcher(Material.DIRT));
        checkInventory(inventory, 2, itemTypeMatcher(Material.DIAMOND_AXE));
        checkInventory(inventory, 1, itemTypeMatcher(Material.DIAMOND));
    }

    @Test
    public void testClearSpecificItemSpecificData() {
        assertTrue(command.execute(opSender, "label",
                new String[]{"ChuckNorris", "minecraft:diamond_axe", "30", "-1"}));
        checkInventory(inventory, 96, itemTypeMatcher(Material.DIRT));
        checkInventory(inventory, 0,
                itemTypeMatcher(Material.DIAMOND_AXE).and(item -> item.getDurability() == 30));
        checkInventory(inventory, 1,
                itemTypeMatcher(Material.DIAMOND_AXE).and(item -> item.getDurability() == 0));
        checkInventory(inventory, 1, itemTypeMatcher(Material.DIAMOND));
    }
}
