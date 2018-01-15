package net.glowstone.block.itemtype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.testutils.ServerShim;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ItemBowTest {

    GlowPlayer player;
    private GlowPlayerInventory inventory;
    private ItemBow bow;
    private ItemStack bowItemStack;
    private World world;
    private Location location;
    private GlowArrow launchedArrow;

    private void scanInventory(boolean expectBow, int expectedBowDamage, int expectedArrows) {
        boolean foundBow = false;
        int arrows = 0;
        for (ItemStack item : inventory.getContents()) {
            switch (item.getType()) {
                case BOW:
                    assertTrue("Unexpected bow found", expectBow);
                    assertFalse("Duplicate bow found", foundBow);
                    foundBow = true;
                    assertEquals((long) expectedBowDamage, (long) item.getDurability());
                    break;
                case ARROW:
                    arrows += item.getAmount();
                    break;
            }
        }
        assertEquals(expectedArrows, arrows);
        if (expectBow) {
            assertTrue("No bow found", foundBow);
        }
    }

    @Before
    public void setUp() {
        ServerShim.install();
        player = Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
        inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        bowItemStack = new ItemStack(Material.BOW);
        inventory.setItemInMainHand(bowItemStack);
        Mockito.doCallRealMethod().when(player).setItemInHand(any(ItemStack.class));
        bow = new ItemBow();
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
        launchedArrow = mock(GlowArrow.class, RETURNS_SMART_NULLS);

        Arrow.Spigot spigot = mock(Arrow.Spigot.class, RETURNS_SMART_NULLS);
        when(launchedArrow.getLocation()).thenReturn(location);
        when(launchedArrow.getVelocity()).thenReturn(new Vector(2, 0, 0));
        when(launchedArrow.spigot()).thenReturn(spigot);
        when(player.launchProjectile(Arrow.class)).thenReturn(launchedArrow);
    }

    @Test
    public void testBasicFunctions() {
        ItemStack arrows = new ItemStack(Material.ARROW, 2);
        inventory.addItem(arrows);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyLong());

        when(player.getUsageTime()).thenReturn(10L);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(Arrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        scanInventory(true, 1, 1);

        // Shooting a second time should consume the last arrow
        bow.startUse(player, bowItemStack);
        bow.endUse(player, bowItemStack);
        scanInventory(true, 2, 0);
    }

    @Test
    public void testNoArrow() {
        // Shouldn't be able to use bow without arrows
        bow.startUse(player, bowItemStack);
        verify(player, never()).setUsageItem(any());
        verify(player, never()).setUsageTime(anyLong());
    }

    @Test
    public void testBreakBow() {
        ItemStack arrows = new ItemStack(Material.ARROW, 1);
        inventory.addItem(arrows);
        bowItemStack.setDurability(Material.BOW.getMaxDurability());
        bow.startUse(player, bowItemStack);
        bow.endUse(player, bowItemStack);
        scanInventory(false, 0, 0);
    }
}
