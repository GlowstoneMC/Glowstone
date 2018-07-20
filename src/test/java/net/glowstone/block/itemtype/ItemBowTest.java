package net.glowstone.block.itemtype;

import static net.glowstone.TestUtils.checkInventory;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.SPECTRAL_ARROW;
import static org.bukkit.Material.TIPPED_ARROW;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Set;
import java.util.function.Predicate;
import net.glowstone.TestUtils;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowSpectralArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.inventory.GlowMetaPotion;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ItemBowTest extends ItemTypeTest {
    public static final Vector POSITIVE_X_DIRECTION = new Vector(1, 0, 0);
    private static final Set<Material> ARROW_TYPES = ImmutableSortedSet.of(
            ARROW, TIPPED_ARROW, SPECTRAL_ARROW);
    private static final Predicate<ItemStack> ARROW_MATCHER
            = TestUtils.itemTypeMatcher(ARROW_TYPES);
    private static final Predicate<ItemStack> BOW_WITH_DAMAGE_1
            = TestUtils.itemTypeMatcher(BOW).and(item -> item.getDurability() == 1);
    private static final Predicate<ItemStack> BOW_WITH_DAMAGE_NOT_1
            = TestUtils.itemTypeMatcher(BOW).and(item -> item.getDurability() != 1);
    private ItemBow bow;
    private ItemStack bowItemStack;
    private GlowArrow launchedArrow;
    private GlowSpectralArrow launchedSpectralArrow;
    private GlowTippedArrow launchedTippedArrow;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        bowItemStack = new ItemStack(BOW);
        inventory.setItemInMainHand(bowItemStack);
        doCallRealMethod().when(player).setItemInHand(any(ItemStack.class));
        bow = new ItemBow();
        launchedArrow = mock(GlowArrow.class, RETURNS_SMART_NULLS);
        launchedSpectralArrow = mock(GlowSpectralArrow.class, RETURNS_SMART_NULLS);
        launchedTippedArrow = mock(GlowTippedArrow.class, RETURNS_SMART_NULLS);
        for (Arrow arrow : new Arrow[]{launchedArrow, launchedSpectralArrow, launchedTippedArrow}) {
            Arrow.Spigot spigot = mock(Arrow.Spigot.class, RETURNS_SMART_NULLS);
            when(arrow.getLocation()).thenReturn(location);
            when(arrow.getVelocity()).thenReturn(POSITIVE_X_DIRECTION);
            when(arrow.spigot()).thenReturn(spigot);
        }
        when(player.getEyeLocation()).thenReturn(
                location.clone().setDirection(POSITIVE_X_DIRECTION));
        doCallRealMethod().when(launchedTippedArrow).copyFrom(any(PotionMeta.class));
        when(player.launchProjectile(Arrow.class)).thenReturn(launchedArrow);
        when(player.launchProjectile(SpectralArrow.class)).thenReturn(launchedSpectralArrow);
        when(player.launchProjectile(TippedArrow.class)).thenReturn(launchedTippedArrow);
    }

    @Override
    protected GlowPlayer mockPlayer() {
        // https://github.com/mockito/mockito/issues/357
        return Mockito.mock(GlowPlayer.class);
    }

    @Test
    public void testBasicFunctions() {
        ItemStack arrows = new ItemStack(Material.ARROW, 2);
        inventory.addItem(arrows);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyInt());

        when(player.getUsageTime()).thenReturn(10);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(Arrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        checkInventory(inventory, 1, BOW_WITH_DAMAGE_1);
        checkInventory(inventory, 0,
                BOW_WITH_DAMAGE_NOT_1);
        checkInventory(inventory, 1,
                item -> ARROW_TYPES.contains(item.getType()));

        // Shooting a second time should consume the last arrow
        bow.startUse(player, bowItemStack);
        bow.endUse(player, bowItemStack);
        checkInventory(inventory, 1,
                item -> item.getType() == BOW && item.getDurability() == 2);
        checkInventory(inventory, 0,
                item -> item.getType() == BOW && item.getDurability() != 2);
        checkInventory(inventory, 0, ARROW_MATCHER);
    }

    @Test
    public void testSpectralArrow() {
        ItemStack arrow = new ItemStack(Material.SPECTRAL_ARROW, 1);
        inventory.addItem(arrow);

        // Should now be able to start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyInt());

        when(player.getUsageTime()).thenReturn(10);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(SpectralArrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        checkInventory(inventory, 1,
                BOW_WITH_DAMAGE_1);
        checkInventory(inventory, 0,
                BOW_WITH_DAMAGE_NOT_1);
        checkInventory(inventory, 0,
                item -> ARROW_TYPES.contains(item.getType()));
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
        verify(player, times(1)).setUsageTime(anyInt());

        when(player.getUsageTime()).thenReturn(10);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(TippedArrow.class);
        verify(launchedTippedArrow, times(1)).setColor(Color.PURPLE);
        verify(launchedTippedArrow, times(1)).setBasePotionData(potionData);
        verify(launchedTippedArrow, times(1)).addCustomEffect(eq(effect), anyBoolean());
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
        checkInventory(inventory, 1,
                BOW_WITH_DAMAGE_1);
        checkInventory(inventory, 0,
                BOW_WITH_DAMAGE_NOT_1);
        checkInventory(inventory, 0,
                item -> ARROW_TYPES.contains(item.getType()));
    }

    @Test
    public void testNoArrow() {
        // Shouldn't be able to use bow without arrows
        bow.startUse(player, bowItemStack);
        verify(player, never()).setUsageItem(any());
        verify(player, never()).setUsageTime(anyInt());
    }

    @Test
    public void testNoArrowCreative() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        // Start shooting
        bow.startUse(player, bowItemStack);
        verify(player, times(1)).setUsageItem(bowItemStack);
        verify(player, times(1)).setUsageTime(anyInt());

        when(player.getUsageTime()).thenReturn(10);
        // Finish shooting
        bow.endUse(player, bowItemStack);
        verify(player, times(1)).launchProjectile(Arrow.class);
        verify(player, times(1)).setUsageItem(null);
        verify(player, times(1)).setUsageTime(0);
    }

    @Test
    public void testBreakBow() {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        inventory.addItem(arrow);
        bowItemStack.setDurability(BOW.getMaxDurability());
        bow.startUse(player, bowItemStack);
        bow.endUse(player, bowItemStack);
        checkInventory(inventory, 0, item -> BOW == item.getType());
        checkInventory(inventory, 0,
                item -> ARROW_TYPES.contains(item.getType()));
    }

    @Test
    public void testInfinity() {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        inventory.addItem(arrow);
        ItemMeta meta = bowItemStack.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowItemStack.setItemMeta(meta);
        bow.startUse(player, bowItemStack);
        bow.endUse(player, bowItemStack);
        checkInventory(inventory, 1, BOW_WITH_DAMAGE_1);
        checkInventory(inventory, 0, BOW_WITH_DAMAGE_NOT_1);
        checkInventory(inventory, 1,
                item -> ARROW_TYPES.contains(item.getType()));
    }
}
