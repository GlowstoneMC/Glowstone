package net.glowstone.block.itemtype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.RequiredArgsConstructor;
import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Superclass of tests of {@link ItemProjectile} subclasses.
 *
 * @param <T> the projectile class that we expect will be passed to
 *            {@link Player#launchProjectile(Class)}
 */
@RequiredArgsConstructor
public abstract class ItemProjectileTest<T extends Projectile> extends ItemTypeTest {
    protected final ItemProjectile item;
    protected final Material type;
    protected final Class<T> projectileClass;
    protected T projectile;

    @Test
    public void testRightClickAir() {
        ItemStack itemStack = new ItemStack(type, 1);
        inventory.setItemInMainHand(itemStack);
        projectile = Mockito.mock(projectileClass);
        when(player.launchProjectile(projectileClass)).thenReturn(projectile);
        item.rightClickAir(player, itemStack);
        verify(player, times(1)).launchProjectile(projectileClass);
        assertEmpty(inventory.getItemInMainHand());
    }

    @Test
    public void testRightClickBlock() {
        GlowBlock block = Mockito.mock(GlowBlock.class);
        ItemStack itemStack = new ItemStack(type, 1);
        inventory.setItemInMainHand(itemStack);
        projectile = Mockito.mock(projectileClass);
        when(player.launchProjectile(projectileClass)).thenReturn(projectile);
        item.rightClickBlock(player, block, BlockFace.UP, itemStack, new Vector(0,0,0), EquipmentSlot.HAND);
        verify(player, times(1)).launchProjectile(projectileClass);
        assertEmpty(inventory.getItemInMainHand());
    }

    @Test
    public void testRightClickAirStackOfTwo() {
        ItemStack itemStack = new ItemStack(type, 2);
        inventory.setItemInMainHand(itemStack);
        projectile = Mockito.mock(projectileClass);
        when(player.launchProjectile(projectileClass)).thenReturn(projectile);
        item.rightClickAir(player, itemStack);
        verify(player, times(1)).launchProjectile(projectileClass);
        ItemStack remaining = inventory.getItemInMainHand();
        assertEquals(1, remaining.getAmount());
        assertEquals(type, remaining.getType());
    }
}
