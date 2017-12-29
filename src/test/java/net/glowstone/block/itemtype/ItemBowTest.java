package net.glowstone.block.itemtype;

import static org.junit.Assert.assertEquals;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.Test;
import org.mockito.Mockito;

public class ItemBowTest {
    @Test
    public void testBasicFunctions() {
        GlowPlayer player = Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
        GlowPlayerInventory inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        ItemStack bowItemStack = new ItemStack(Material.BOW);
        inventory.addItem(bowItemStack);
        ItemBow bow = new ItemBow();

        // Shouldn't be able to use bow without arrows
        bow.startUse(player, bowItemStack);
        verify(player, never()).setUsageItem(any());
        verify(player, never()).setUsageTime(anyLong());

        ItemStack arrows = new ItemStack(Material.ARROW, 2);
        inventory.addItem(arrows);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyLong());
        // FIXME: assertEquals(1, arrows.getAmount());

        when(player.getUsageTime()).thenReturn(10L);
        World world = mock(World.class, RETURNS_SMART_NULLS);
        Location location = new Location(world, 0, 0, 0);
        GlowArrow launchedArrow = mock(GlowArrow.class, RETURNS_SMART_NULLS);
        Arrow.Spigot spigot = mock(Arrow.Spigot.class, RETURNS_SMART_NULLS);
        when(launchedArrow.getLocation()).thenReturn(location);
        when(launchedArrow.getVelocity()).thenReturn(new Vector(2, 0, 0));
        when(launchedArrow.spigot()).thenReturn(spigot);
        when(player.launchProjectile(Arrow.class)).thenReturn(launchedArrow);

        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(Arrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        assertEquals(1, bowItemStack.getDurability()); // shooting should damage the bow
    }

}