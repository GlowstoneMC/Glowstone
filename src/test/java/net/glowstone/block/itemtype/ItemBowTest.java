package net.glowstone.block.itemtype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowSpectralArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.inventory.GlowMetaPotion;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.testutils.ServerShim;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
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
    private GlowSpectralArrow launchedSpectralArrow;
    private GlowTippedArrow launchedTippedArrow;

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
                case SPECTRAL_ARROW:
                case TIPPED_ARROW:
                    arrows += item.getAmount();
                    break;
                default:
                    // do nothing
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
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        bowItemStack = new ItemStack(Material.BOW);
        inventory.setItemInMainHand(bowItemStack);
        Mockito.doCallRealMethod().when(player).setItemInHand(any(ItemStack.class));
        bow = new ItemBow();
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
        launchedArrow = mock(GlowArrow.class, RETURNS_SMART_NULLS);
        launchedSpectralArrow = mock(GlowSpectralArrow.class, RETURNS_SMART_NULLS);
        launchedTippedArrow = mock(GlowTippedArrow.class, RETURNS_SMART_NULLS);
        for (Arrow arrow : new Arrow[]{launchedArrow, launchedSpectralArrow, launchedTippedArrow}) {
            Arrow.Spigot spigot = mock(Arrow.Spigot.class, RETURNS_SMART_NULLS);
            when(arrow.getLocation()).thenReturn(location);
            when(arrow.getVelocity()).thenReturn(new Vector(2, 0, 0));
            when(arrow.spigot()).thenReturn(spigot);
        }
        when(player.launchProjectile(Arrow.class)).thenReturn(launchedArrow);
        when(player.launchProjectile(SpectralArrow.class)).thenReturn(launchedSpectralArrow);
        when(player.launchProjectile(TippedArrow.class)).thenReturn(launchedTippedArrow);
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
    public void testSpectralArrow() {
        ItemStack arrow = new ItemStack(Material.SPECTRAL_ARROW, 1);
        inventory.addItem(arrow);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyLong());

        when(player.getUsageTime()).thenReturn(10L);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(SpectralArrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        scanInventory(true, 1, 0);
    }

    @Test
    public void testTippedArrow() {
        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 1);
        GlowMetaPotion potion = new GlowMetaPotion(arrow.getItemMeta());
        potion.setColor(Color.PURPLE);
        final PotionData potionData = new PotionData(PotionType.FIRE_RESISTANCE);
        potion.setBasePotionData(potionData);
        final PotionEffect effect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10, 1);
        potion.addCustomEffect(effect, false);
        assertTrue(arrow.setItemMeta(potion));
        inventory.addItem(arrow);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyLong());

        when(player.getUsageTime()).thenReturn(10L);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(TippedArrow.class);
        /* FIXME: Failing
        verify(launchedTippedArrow, times(1)).setColor(Color.PURPLE);
        verify(launchedTippedArrow, times(1)).setBasePotionData(potionData);
        verify(launchedTippedArrow, times(1)).addCustomEffect(eq(effect), anyBoolean());
        */
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        scanInventory(true, 1, 0);
    }

    @Test
    public void testNoArrow() {
        // Shouldn't be able to use bow without arrows
        bow.startUse(player, bowItemStack);
        verify(player, never()).setUsageItem(any());
        verify(player, never()).setUsageTime(anyLong());
    }

    @Test
    public void testNoArrowCreative() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        // Start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyLong());

        when(player.getUsageTime()).thenReturn(10L);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(Arrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
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
