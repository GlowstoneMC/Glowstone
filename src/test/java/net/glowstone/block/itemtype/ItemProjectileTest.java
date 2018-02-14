package net.glowstone.block.itemtype;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Superclass of tests of {@link ItemProjectile} subclasses.
 *
 * @param <T> the projectile class that we expect will be passed to
 *         {@link Player#launchProjectile(Class)}
 */
@RequiredArgsConstructor
public abstract class ItemProjectileTest<T extends Projectile> extends ItemTypeTest {
    protected final ItemProjectile item;
    protected final Material type;
    protected final Class<T> projectileClass;

    @SuppressWarnings("unchecked")
    @Test
    public void testUse() {
        ItemStack itemStack = new ItemStack(type, 1);
        inventory.setItemInMainHand(itemStack);
        Projectile projectile = Mockito.mock(projectileClass);
        when(player.launchProjectile(projectileClass)).thenReturn((T) projectile);
        item.use(player, itemStack);
        checkProjectile((T) projectile);
    }

    protected void checkProjectile(T projectile) {
        verify(player, times(1)).launchProjectile(projectileClass);
        // FIXME: assertEmpty(inventory.getItemInMainHand());
    }
}
