package net.glowstone.block.itemtype;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowSpectralArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.testutils.ServerShim;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Superclass for tests of {@link ItemType} subclasses. Provides a mock player with a real inventory
 * and a location in a mock world.
 */
public abstract class ItemTypeTest {
    protected GlowPlayerInventory inventory;
    protected GlowPlayer player;
    protected World world;
    protected Location location;

    @Before
    public void setUp() {
        ServerShim.install();
        player = Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
        inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
    }
}
